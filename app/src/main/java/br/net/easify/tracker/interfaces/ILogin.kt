package br.net.easify.tracker.interfaces

import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface ILogin {
    @POST("/v1/login")
    fun login(@Body data: LoginBody): Single<Token>
}