package br.net.easify.tracker.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.database.model.DbToken
import br.net.easify.tracker.database.model.DbUser
import br.net.easify.tracker.model.ErrorResponse
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User
import br.net.easify.tracker.services.LoginService
import br.net.easify.tracker.services.UserService
import com.auth0.android.jwt.JWT
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

    private var loginService = LoginService()
    private var userService = UserService()

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
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

    fun saveTokens(tokens: DbToken) {
        database.tokenDao().delete()
        database.tokenDao().insert(tokens)
    }

    fun saveLoggedUser(user: User) {

        val dbUser = DbUser(
            user.userId,
            user.userName,
            user.userEmail,
            "",
            user.userAvatar,
            user.userActive,
            user.userCreatedAt,
            user.token,
            user.refreshToken
        )

        database.userDao().delete()
        database.userDao().insert(dbUser)
    }

    fun getLoggedUser(): DbUser? {
        return database.userDao().getLoggedUser()
    }

    fun getUserFromToken(context: Context) {

        val dbToken = database.tokenDao().get()
        dbToken?.let { tokens: DbToken ->
            val jwt = JWT(tokens.token)
            val userId = jwt.getClaim("userId").asInt()

            userService.setTokens(dbToken)

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