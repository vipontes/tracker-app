package br.net.easify.tracker.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.R
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.di.component.DaggerMainComponent
import br.net.easify.tracker.di.module.AppModule
import br.net.easify.tracker.model.ErrorResponse
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.services.LoginService
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
    val errorResponse by lazy { MutableLiveData<ErrorResponse>() }
    val loginBody by lazy { MutableLiveData<LoginBody>() }

    @Inject
    lateinit var loginService: LoginService

    @Inject
    lateinit var database: AppDatabase

    init {
        DaggerMainComponent.builder()
            .appModule(AppModule(application))
            .build()
            .inject(this)

        loginBody.value = LoginBody("", "")
    }

    fun login(context: Context) {

        loginBody.value.let {
            val email = it?.user_email.toString()
            val password = it?.user_password.toString()
            if (validate(context)) {
                disposable.add(
                    loginService.login(email, password)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Token>() {
                            override fun onSuccess(res: Token) {
                                tokens.value = res
                            }
                            override fun onError(e: Throwable) {
                                e.printStackTrace()

                                if (e is HttpException) {
                                    val errorJsonString = e.response().errorBody()?.string()
                                    val message = JsonParser().parse(errorJsonString)
                                        .asJsonObject["message"]
                                        .asString

                                    errorResponse.value = ErrorResponse(message)
                                } else {
                                    errorResponse.value = ErrorResponse(context.getString(R.string.internal_error))
                                }
                            }
                        })
                )
            }
        }
    }

    private fun validate(context: Context): Boolean {

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

        return false
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}