package br.net.easify.tracker.interfaces

import br.net.easify.tracker.model.Route
import br.net.easify.tracker.model.RoutePost
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface IRoute {
    @POST("/v1/route")
    fun postRoute(@Body data: RoutePost): Single<Route>
}