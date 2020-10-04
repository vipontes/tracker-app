package br.net.easify.tracker.api.interceptor

import android.app.Application
import br.net.easify.database.AppDatabase
import br.net.easify.database.model.SqliteToken
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.model.RefreshTokenBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.api.RetrofitBuilder
import br.net.easify.tracker.api.interfaces.ILogin
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    var application: Application,
    private val retrofitBuilder: RetrofitBuilder
) : Interceptor {

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
                    throw java.lang.RuntimeException(
                        application.getString(R.string.token_not_found))
                } else {
                    requestBuilder.addHeader(
                        "Authorization",
                        "Bearer ${tokens.token}"
                    )
                    val initialResponse = chain.proceed(requestBuilder.build())
                    when {
                        initialResponse.code() == 401 -> {
                            val responseNewTokenLoginModel = runBlocking {
                                val body =
                                    RefreshTokenBody(tokens.refresh_token)
                                retrofitBuilder.retrofit()
                                    .create(ILogin::class.java)
                                    .refreshToken(body).execute()
                            }

                            return when {
                                (responseNewTokenLoginModel == null ||
                                        responseNewTokenLoginModel.code() != 200) -> {
                                    database.tokenDao().delete()
                                    return initialResponse
                                }
                                else -> {
                                    responseNewTokenLoginModel.body()?.let {
                                        updateToken(it)
                                    }

                                    val newAuthenticationRequest =
                                        originalRequest.newBuilder()
                                            .addHeader(
                                                "Authorization",
                                                "Bearer ${tokens.token}"
                                            )
                                            .build()

                                    chain.proceed(newAuthenticationRequest)
                                }
                            }
                        }
                        else -> {
                            return initialResponse
                        }
                    }
                }
            }

            return chain.proceed(requestBuilder.build())
        }
    }

    private fun updateToken(token: Token) {
        this.tokens = fromToken(token)
        database.tokenDao().delete()
        database.tokenDao().insert(this.tokens)
    }

    private fun getToken() {
        val dbToken = database.tokenDao().get()
        dbToken?.let {
            tokens = it
        }
    }

    fun fromToken(token: Token): SqliteToken = SqliteToken(token.token, token.refreshToken)
}
