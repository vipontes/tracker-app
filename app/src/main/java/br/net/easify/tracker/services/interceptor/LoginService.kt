package br.net.easify.tracker.services.interceptor

import br.net.easify.tracker.interfaces.ILogin
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.utils.Constants
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class LoginService {
    private val api = Retrofit.Builder()
        .baseUrl(Constants.apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ILogin::class.java)

    fun login(email: String, senha: String): Single<Token> {

        val loginBody = LoginBody(email, senha)
        return api.login(loginBody)
    }
}