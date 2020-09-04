package br.net.easify.tracker.api

import android.app.Application
import br.net.easify.tracker.model.Route
import br.net.easify.tracker.model.RoutePost
import br.net.easify.tracker.api.interfaces.IRoute
import br.net.easify.tracker.model.Response
import io.reactivex.Single

class RouteService(application: Application) {

    private val api = RetrofitBuilder(application).retrofitAuth()
        .create(IRoute::class.java)

    fun postRoute(data: RoutePost): Single<Route> {
        return api.postRoute(data)
    }

    fun getRoutesByUser(userId: Long): Single<List<Route>> {
        return api.getRoutesByUser(userId)
    }

    fun deleteRoute(routeId: Long): Single<Response> {
        return api.deleteRoute(routeId)
    }
}