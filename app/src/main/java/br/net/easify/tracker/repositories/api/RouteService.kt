package br.net.easify.tracker.repositories.api

import android.app.Application
import br.net.easify.tracker.repositories.api.interceptor.AuthInterceptor
import br.net.easify.tracker.helpers.Constants
import br.net.easify.tracker.repositories.api.interfaces.IRoute
import br.net.easify.tracker.model.Route
import br.net.easify.tracker.model.RoutePost
import io.reactivex.Single
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RouteService(application: Application) {

    private val api = RetrofitBuilder(application).retrofitAuth()
        .create(IRoute::class.java)

    fun postRoute(data: RoutePost): Single<Route> {
        return api.postRoute(data)
    }

    fun getRoutesByUser(userId: Long): Single<List<Route>> {
        return api.getRoutesByUser(userId)
    }
}