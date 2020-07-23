package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.database.model.TokenLocal
import br.net.easify.tracker.di.component.DaggerViewModuleComponent
import br.net.easify.tracker.di.module.AppModule
import br.net.easify.tracker.services.LoginService
import br.net.easify.tracker.services.UserService
import br.net.easify.tracker.services.interceptor.AuthInterceptor
import br.net.easify.tracker.utils.SharedPreferencesHelper
import com.auth0.android.jwt.JWT
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    @Inject
    lateinit var loginService: LoginService

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var prefs: SharedPreferencesHelper

    @Inject
    lateinit var interceptor: AuthInterceptor

    init {
        DaggerViewModuleComponent.builder()
            .appModule(AppModule(application))
            .build()
            .inject(this)

        initInterceptor()
    }

    private fun initInterceptor() {
        val tokenLocal = database.tokenDao().get()
        tokenLocal?.let { tokens: TokenLocal ->
            interceptor.setTokens(TokenLocal(tokens.token, tokens.refresh_token))
        }
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}