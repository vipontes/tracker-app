package br.net.easify.tracker.api.interfaces

import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.User
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface IUser {
    @GET("user/{userId}")
    fun getUser(@Path ("userId") userId: Int): Single<User>

    @PUT("user")
    fun update(@Body data: User): Single<Response>
}