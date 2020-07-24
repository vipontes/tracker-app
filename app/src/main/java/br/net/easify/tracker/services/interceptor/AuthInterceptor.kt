package br.net.easify.tracker.services.interceptor

import br.net.easify.tracker.database.model.TokenLocal
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class AuthInterceptor @Inject constructor(private var tokens: TokenLocal) : Interceptor {

    fun setTokens(tokens: TokenLocal) {
        this.tokens = tokens
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        synchronized(this) {
            val request: Request = chain.request()

            val requestBuilder: Request.Builder = request.newBuilder()

            if (request.header("No-Authentication") == null) {
                if (tokens == null) {
                    throw java.lang.RuntimeException("Token not found")
                } else {
                    requestBuilder.addHeader("Authorization", "Bearer ${tokens?.token}")
                }
            }

            return chain.proceed(requestBuilder.build())
        }
    }
}
