package br.net.easify.tracker.services

import android.app.Application
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.database.model.TokenLocal
import br.net.easify.tracker.interfaces.ILogin
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.services.interceptor.AuthInterceptor
import br.net.easify.tracker.utils.Constants
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LoginService() {

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