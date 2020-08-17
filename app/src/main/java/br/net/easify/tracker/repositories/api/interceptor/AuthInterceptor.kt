package br.net.easify.tracker.repositories.api.interceptor

import android.app.Application
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.DbToken
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(application: Application) : Interceptor {

    @Inject
    lateinit var database: AppDatabase

    private var tokens = DbToken("", "")

    init {
        (application as MainApplication).getAppComponent()?.inject(this)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        synchronized(this) {
            val request: Request = chain.request()

            getToken()

            val requestBuilder: Request.Builder = request.newBuilder()

            if (request.header("No-Authentication") == null) {
                if (tokens.token.isEmpty()) {
                    throw java.lang.RuntimeException("Token not found")
                } else {
                    requestBuilder.addHeader("Authorization", "Bearer ${tokens.token}")
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