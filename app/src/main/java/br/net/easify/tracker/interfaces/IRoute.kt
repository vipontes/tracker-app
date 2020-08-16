package br.net.easify.tracker.interfaces

import br.net.easify.tracker.model.Route
import br.net.easify.tracker.model.RoutePost
import br.net.easify.tracker.model.User
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IRoute {
    @POST("/v1/route")
    fun postRoute(@Body data: RoutePost): Single<Route>

    @GET("/v1/routes/{userId}")
    fun getRoutesByUser(@Path("userId") userId: Int): Single<List<Route>>
}