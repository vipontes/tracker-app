package br.net.easify.tracker.api

import br.net.easify.tracker.api.interceptor.AuthInterceptor
import br.net.easify.tracker.database.model.DbToken
import br.net.easify.tracker.helpers.Constants
import br.net.easify.tracker.interfaces.IRoute
import br.net.easify.tracker.interfaces.IUser
import br.net.easify.tracker.model.Route
import br.net.easify.tracker.model.RoutePost
import br.net.easify.tracker.model.User
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RouteService() {
    private var interceptor: AuthInterceptor = AuthInterceptor(DbToken("", ""))

    fun setTokens(tokens: DbToken) {
        this.interceptor.setTokens(tokens)
    }

    private val api = Retrofit.Builder()
        .baseUrl(Constants.apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(addHttpInterceptor())
        .build()
        .create(IRoute::class.java)

    private fun addHttpInterceptor(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(this.interceptor)
        return okHttpClientBuilder.build()
    }

    fun postRoute(data: RoutePost): Single<Route> {
        return api.postRoute(data)
    }
}