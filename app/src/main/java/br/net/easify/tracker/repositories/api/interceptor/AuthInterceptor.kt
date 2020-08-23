package br.net.easify.tracker.repositories.api.interceptor

import android.app.Application
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.model.RefreshTokenBody
import br.net.easify.tracker.repositories.api.LoginService
import br.net.easify.tracker.repositories.api.RetrofitBuilder
import br.net.easify.tracker.repositories.api.interfaces.ILogin
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.SqliteToken
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(var application: Application, private val retrofitBuilder: RetrofitBuilder) : Interceptor {

    @Inject
    lateinit var database: AppDatabase

    private var tokens = SqliteToken("", "")

    init {
        (application as MainApplication).getAppComponent()?.inject(this)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        synchronized(this) {
            val originalRequest: Request = chain.request()

            getToken()

            val requestBuilder: Request.Builder = originalRequest.newBuilder()

            if (originalRequest.header("No-Authentication") == null) {
                if (tokens.token.isEmpty()) {
                    throw java.lang.RuntimeException(application.getString(R.string.token_not_found))
                } else {
                    requestBuilder.addHeader("Authorization", "Bearer ${tokens.token}")
                    val initialResponse = chain.proceed(requestBuilder.build())
                    when {
                        initialResponse.code() == 401 -> {
                            val responseNewTokenLoginModel = runBlocking {
                                val body = RefreshTokenBody(tokens.refresh_token)
                                retrofitBuilder.retrofit().create(ILogin::class.java).refreshToken(body).execute()
                                }

                            return when {
                                responseNewTokenLoginModel == null || responseNewTokenLoginModel.code() != 200 -> {
                                    return initialResponse
                                }
                                else -> {
                                    responseNewTokenLoginModel.body()?.let {
                                        tokens = SqliteToken().fromToken(it)
                                        database.tokenDao().delete()
                                        database.tokenDao().insert(tokens)
                                    }

                                    val newAuthenticationRequest =
                                        originalRequest.newBuilder()
                                            .addHeader("Authorization", "Bearer ${tokens.token}")
                                            .build()

                                    chain.proceed(newAuthenticationRequest)
                                }
                            }
                        }
                        else -> return initialResponse
                    }
                }
            }

            return chain.proceed(requestBuilder.build())
        }
    }

    private fun getToken() {
        val dbToken = database.tokenDao().get()
        dbToken?.let {
            tokens = it
        }
    }
}
