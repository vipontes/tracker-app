package br.net.easify.tracker.api

import android.app.Application
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.api.interfaces.ILogin
import io.reactivex.Single

class LoginService (application: Application) {

    private val api = RetrofitBuilder(application).retrofit()
        .create(ILogin::class.java)

    fun login(email: String, senha: String): Single<Token> {
        val body = LoginBody(email, senha)
        return api.login(body)
    }
}