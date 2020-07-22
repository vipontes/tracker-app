package br.net.easify.tracker.interfaces

import br.net.easify.tracker.model.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface IUser {
    @GET("/v1/user/{userId}")
    fun getUser(@Path ("userId") userId: Int): Single<User>
}