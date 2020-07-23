package br.net.easify.tracker.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.R
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.database.model.TokenLocal
import br.net.easify.tracker.di.component.DaggerViewModuleComponent
import br.net.easify.tracker.di.module.AppModule
import br.net.easify.tracker.model.ErrorResponse
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User
import br.net.easify.tracker.services.LoginService
import br.net.easify.tracker.services.UserService
import br.net.easify.tracker.services.interceptor.AuthInterceptor
import br.net.easify.tracker.utils.SharedPreferencesHelper
import com.auth0.android.jwt.JWT
import com.google.gson.JsonParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val disposable = CompositeDisposable()

    val tokens by lazy { MutableLiveData<Token>() }
    val loggedUser by lazy { MutableLiveData<User>() }
    val errorResponse by lazy { MutableLiveData<ErrorResponse>() }
    val loginBody by lazy { MutableLiveData<LoginBody>() }

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

        loginBody.value = LoginBody("", "")
    }

    fun login(context: Context) {
        loginBody.value.let {
            val email = it?.user_email.toString()
            val password = it?.user_password.toString()
            checkLogin(context, email, password)
        }
    }

    private fun checkLogin(context: Context, email: String, password: String) {

        disposable.add(
            loginService.login(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Token>() {
                    override fun onSuccess(res: Token) {
                        tokens.value = res
                        interceptor.setTokens(TokenLocal(res.token, res.refreshToken))
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        if (e is HttpException) {
                            if (e.code() == 401) {
                                ErrorResponse(context.getString(R.string.unauthorized))
                            } else {
                                ErrorResponse(context.getString(R.string.internal_error))
                            }
                        } else {
                            errorResponse.value =
                                ErrorResponse(context.getString(R.string.internal_error))
                        }
                    }
                })
        )
    }

    fun saveTokens(tokens: TokenLocal) {
        database.tokenDao().delete()
        database.tokenDao().insert(tokens)
    }

    fun getUserFromToken(context: Context) {

        val tokenLocal = database.tokenDao().get()
        tokenLocal?.let {tokens: TokenLocal ->
            val jwt = JWT(tokens.token)
            val userId = jwt.getClaim("userId").asInt()

            interceptor.setTokens(TokenLocal(tokens.token, tokens.refresh_token))

            userId?.let { id: Int ->
                disposable.add(
                    userService.getUser(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<User>() {
                            override fun onSuccess(res: User) {
                                loggedUser.value = res
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()

                                if (e is HttpException) {
                                    if (e.code() == 401) {
                                        ErrorResponse(context.getString(R.string.unauthorized))
                                    } else {
                                        ErrorResponse(context.getString(R.string.internal_error))
                                    }
                                } else {
                                    errorResponse.value =
                                        ErrorResponse(context.getString(R.string.internal_error))
                                }
                            }

                        })
                )
            }
        }
    }

    fun validate(context: Context): Boolean {

        loginBody.value.let {
            val email = it?.user_email
            val password = it?.user_password

            if (email.isNullOrEmpty()) {
                errorResponse.value = ErrorResponse(context.getString(R.string.empty_email))
                return false
            }

            if (password.isNullOrEmpty()) {
                errorResponse.value = ErrorResponse(context.getString(R.string.empty_password))
                return false
            }

            return true
        }
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}