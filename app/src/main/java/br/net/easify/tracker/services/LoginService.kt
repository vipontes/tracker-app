package br.net.easify.tracker.services

import br.net.easify.tracker.di.component.DaggerAuthComponent
import br.net.easify.tracker.interfaces.ILogin
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import io.reactivex.Single
import javax.inject.Inject

class LoginService {

    @Inject
    lateinit var api: ILogin

    init {
        DaggerAuthComponent.create().inject(this)
    }

    fun login(email: String, senha: String): Single<Token> {

        val loginBody = LoginBody(email, senha)
        return api.login(loginBody)
    }
}