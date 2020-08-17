package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User
import br.net.easify.tracker.repositories.api.LoginService
import br.net.easify.tracker.repositories.api.UserService
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.DbToken
import br.net.easify.tracker.repositories.database.model.DbUser
import com.auth0.android.jwt.JWT
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class UserRepository(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()
    private val userData = MutableLiveData<DbUser>()
    private val tokens = MutableLiveData<Token>()
    private val errorResponse = MutableLiveData<Response>()
    private val loggedUser = MutableLiveData<User>()
    private var loginService = LoginService()
    private var userService = UserService(application)

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun getTokens(): MutableLiveData<Token> {
        return tokens
    }

    fun getErrorResponse(): MutableLiveData<Response> {
        return errorResponse
    }

    fun getUser(): MutableLiveData<User> {
        return loggedUser
    }

    fun checkLogin(email: String, password: String) {

        disposable.add(
            loginService.login(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Token>() {
                    override fun onSuccess(res: Token) {
                        tokens.value = res

                        val tokenLocal = DbToken(res.token, res.refreshToken)
                        saveTokens(tokenLocal)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        if (e is HttpException) {
                            if (e.code() == 401) {
                                errorResponse.value =
                                    Response((getApplication() as MainApplication).getString(R.string.unauthorized))
                            } else {
                                errorResponse.value =
                                    Response((getApplication() as MainApplication).getString(R.string.internal_error))
                            }
                        } else {
                            errorResponse.value =
                                Response((getApplication() as MainApplication).getString(R.string.internal_error))
                        }
                    }
                })
        )
    }

    fun saveTokens(tokens: DbToken) {
        deleteToken()
        insertToken(tokens)
    }

    fun getUserFromToken() {

        val dbToken = getToken()
        dbToken?.let { tokens: DbToken ->
            val jwt = JWT(tokens.token)
            val userId = jwt.getClaim("userId").asInt()
            userId?.let { id: Int ->
                disposable.add(
                    userService.getUser(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<User>() {
                            override fun onSuccess(res: User) {
                                loggedUser.value = res
                                saveLoggedUser(res)
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()

                                if (e is HttpException) {
                                    if (e.code() == 401) {
                                        errorResponse.value =
                                            Response((getApplication() as MainApplication).getString(R.string.unauthorized))
                                    } else {
                                        errorResponse.value =
                                            Response((getApplication() as MainApplication).getString(R.string.internal_error))
                                    }
                                } else {
                                    errorResponse.value =
                                        Response((getApplication() as MainApplication).getString(R.string.internal_error))
                                }
                            }
                        })
                )
            }
        }
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
            user.userWeight,
            user.token,
            user.refreshToken
        )

        delete()
        insert(dbUser)
    }

    fun getUserData(): MutableLiveData<DbUser> {
        val loggedUser = getLoggedUser()
        loggedUser?.let {
            userData.value = it
        }
        return userData
    }

    fun getUser(userId: Long): DbUser? {
        return database.userDao().getUser(userId)
    }

    fun getLoggedUser(): DbUser? {
        return database.userDao().getLoggedUser()
    }

    fun insert(dbUser: DbUser): Long {
        return database.userDao().insert(dbUser)
    }

    fun delete(userId: Long) = database.userDao().delete(userId)

    fun delete() {
        database.userDao().delete()
        userData.value = null
    }

    fun logout() {
        database.userDao().delete()
        database.routeDao().delete()
        database.routePathDao().delete()
        database.tokenDao().delete()
        userData.value = null
    }

    fun update(dbUser: DbUser) = database.userDao().update(dbUser)

    fun getToken(): DbToken? = database.tokenDao().get()

    fun insertToken(dbToken: DbToken) = database.tokenDao().insert(dbToken)

    fun deleteToken() = database.tokenDao().delete()

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}