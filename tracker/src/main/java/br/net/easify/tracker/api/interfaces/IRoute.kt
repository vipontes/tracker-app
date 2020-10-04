package br.net.easify.tracker.api.interfaces

import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.Route
import br.net.easify.tracker.model.RoutePost
import io.reactivex.Single
import retrofit2.http.*

interface IRoute {
    @POST("route")
    fun postRoute(@Body data: RoutePost): Single<Route>

    @GET("routes/{userId}")
    fun getRoutesByUser(@Path("userId") userId: Long): Single<List<Route>>

    @DELETE("route/{routeId}")
    fun deleteRoute(@Path("routeId") routeId: Long): Single<Response>
}