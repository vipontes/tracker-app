package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.interfaces.ILocalUser
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.api.LoginService
import br.net.easify.tracker.api.UserService
import br.net.easify.tracker.repositories.database.model.SqliteToken
import br.net.easify.tracker.repositories.database.model.SqliteUser
import com.auth0.android.jwt.JWT
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class LoginViewModel(application: Application) : AndroidViewModel(application), ILocalUser {
    private val disposable = CompositeDisposable()

    val loginBody by lazy { MutableLiveData<LoginBody>() }
    var userData = MutableLiveData<SqliteUser>()
    var tokens = MutableLiveData<Token>()
    var errorResponse = MutableLiveData<Response>()

    private var loginService = LoginService(application)
    private var userService = UserService(application)

    @Inject
    lateinit var userRepository: UserRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        loginBody.value = LoginBody("", "")
        getLoggedUser()
    }

    fun login() {
        loginBody.value.let {
            val email = it?.user_email.toString()
            val password = it?.user_password.toString()
            checkLogin(email, password)
        }
    }

    private fun checkLogin(email: String, password: String) {
        disposable.add(
            loginService.login(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Token>() {
                    override fun onSuccess(res: Token) {
                        userRepository.saveTokens(res)
                        tokens.value = res
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        if (e is HttpException) {
                            if (e.code() == 401) {
                                errorResponse.value =
                                    Response(
                                        false,
                                        (getApplication() as MainApplication).getString(
                                            R.string.unauthorized
                                        )
                                    )
                            } else {
                                errorResponse.value =
                                    Response(
                                        false,
                                        (getApplication() as MainApplication).getString(
                                            R.string.internal_error
                                        )
                                    )
                            }
                        } else {
                            errorResponse.value =
                                Response(
                                    false,
                                    (getApplication() as MainApplication).getString(
                                        R.string.internal_error
                                    )
                                )
                        }
                    }
                })
        )
    }

    fun getUserFromToken() {
        val dbToken = userRepository.getToken()
        dbToken?.let { tokens: SqliteToken ->
            val jwt = JWT(tokens.token)
            val userId = jwt.getClaim("userId").asInt()
            userId?.let { id: Int ->
                disposable.add(
                    userService.getUser(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object :
                            DisposableSingleObserver<User>() {
                            override fun onSuccess(res: User) {
                                userRepository.saveLoggedUser(res)
                                userData.value = SqliteUser().fromUser(res)
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()

                                if (e is HttpException) {
                                    if (e.code() == 401) {
                                        errorResponse.value =
                                            Response(
                                                false,
                                                (getApplication() as MainApplication).getString(
                                                    R.string.unauthorized
                                                )
                                            )
                                    } else {
                                        errorResponse.value =
                                            Response(
                                                false,
                                                (getApplication() as MainApplication).getString(
                                                    R.string.internal_error
                                                )
                                            )
                                    }
                                } else {
                                    errorResponse.value =
                                        Response(
                                            false,
                                            (getApplication() as MainApplication).getString(
                                                R.string.internal_error
                                            )
                                        )
                                }
                            }
                        })
                )
            }
        }
    }



    fun validate(): Boolean {

        loginBody.value.let {
            val email = it?.user_email
            val password = it?.user_password

            if (email.isNullOrEmpty()) {
                errorResponse.value =
                    Response((getApplication() as MainApplication).getString(R.string.empty_email))
                return false
            }

            if (password.isNullOrEmpty()) {
                errorResponse.value =
                    Response((getApplication() as MainApplication).getString(R.string.empty_password))
                return false
            }

            return true
        }
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }

    override fun getLoggedUser() {
        userData.value = userRepository.getLoggedUser()
    }
}