package br.net.easify.tracker.api

import android.app.Application
import br.net.easify.tracker.api.interfaces.IRoutePath
import br.net.easify.tracker.model.RoutePath
import io.reactivex.Single

class RoutePathService(application: Application) {

    private val api = RetrofitBuilder(application)
        .retrofitAuth()
        .create(IRoutePath::class.java)

    fun getPathFromRoute(routeId: Long): Single<List<RoutePath>> {
        return api.getPathFromRoute(routeId)
    }
}