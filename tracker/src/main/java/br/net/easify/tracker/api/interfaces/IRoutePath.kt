package br.net.easify.tracker.api.interfaces

import br.net.easify.tracker.model.RoutePath
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface IRoutePath {
    @GET("routepath/{routeId}")
    fun getPathFromRoute(@Path("routeId") routeId: Long): Single<List<RoutePath>>
}