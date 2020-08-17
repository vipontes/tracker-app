package br.net.easify.tracker.repositories.api.interfaces

import br.net.easify.tracker.model.Route
import br.net.easify.tracker.model.RoutePost
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IRoute {
    @POST("route")
    fun postRoute(@Body data: RoutePost): Single<Route>

    @GET("routes/{userId}")
    fun getRoutesByUser(@Path("userId") userId: Long): Single<List<Route>>
}