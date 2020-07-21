package br.net.easify.tracker.services

import br.net.easify.tracker.di.component.DaggerApiComponent
import br.net.easify.tracker.di.component.DaggerAppComponent
import br.net.easify.tracker.interfaces.ILogin
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.utils.Constants
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LoginService {

    @Inject
    lateinit var api: ILogin

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun login(email: String, senha: String): Single<Token> {

        val loginBody = LoginBody(email, senha)
        return api.login(loginBody)
    }
}