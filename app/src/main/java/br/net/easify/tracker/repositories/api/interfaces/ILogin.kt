package br.net.easify.tracker.repositories.api.interfaces

import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.RefreshTokenBody
import br.net.easify.tracker.model.Token
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ILogin {
    @POST("login")
    @Headers("No-Authentication: true")
    fun login(@Body data: LoginBody): Single<Token>

    @POST("refresh-token")
    @Headers("No-Authentication: true")
    fun refreshToken(@Body data: RefreshTokenBody): Call<Token>
}