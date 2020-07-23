package br.net.easify.tracker.services

import br.net.easify.tracker.R
import br.net.easify.tracker.database.model.TokenLocal
import br.net.easify.tracker.di.component.DaggerApiComponent
import br.net.easify.tracker.interfaces.ILogin
import br.net.easify.tracker.model.ErrorResponse
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User
import br.net.easify.tracker.services.interceptor.AuthInterceptor
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class LoginService {

    @Inject
    lateinit var api: ILogin

    @Inject
    lateinit var interceptor: AuthInterceptor

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun login(email: String, senha: String): Single<Token> {

        val loginBody = LoginBody(email, senha)

        val tokens = api.login(loginBody)

        val subscribeWith = tokens.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Token>() {
                override fun onSuccess(res: Token) {
                    interceptor.setTokens(TokenLocal(res.token, res.refreshToken))
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

        return tokens
    }
}