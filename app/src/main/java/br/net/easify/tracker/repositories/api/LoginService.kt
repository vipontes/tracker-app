package br.net.easify.tracker.repositories.api

import android.app.Application
import br.net.easify.tracker.repositories.api.interfaces.ILogin
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.helpers.Constants
import br.net.easify.tracker.model.RefreshTokenBody
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class LoginService (application: Application) {

    private val api = RetrofitBuilder(application).retrofit()
        .create(ILogin::class.java)

    fun login(email: String, senha: String): Single<Token> {
        val body = LoginBody(email, senha)
        return api.login(body)
    }

    fun refreshToken(refreshToken: String): Call<Token> {
        val body = RefreshTokenBody(refreshToken)
        return api.refreshToken(body)
    }
}