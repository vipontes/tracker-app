package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.repositories.database.model.DbUser
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val disposable = CompositeDisposable()

    val loginBody by lazy { MutableLiveData<LoginBody>() }
    val error by lazy { MutableLiveData<Response>() }

    @Inject
    lateinit var userRepository: UserRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        loginBody.value = LoginBody("", "")
    }

    fun login() {
        loginBody.value.let {
            val email = it?.user_email.toString()
            val password = it?.user_password.toString()
            userRepository.checkLogin(email, password)
        }
    }

    fun getTokens(): MutableLiveData<Token> = userRepository.getTokens()

    fun getErrorResponse(): MutableLiveData<Response> = userRepository.getErrorResponse()

    fun getUser(): MutableLiveData<User> = userRepository.getUser()

    fun getUserFromToken() = userRepository.getUserFromToken()

    fun getLoggedUser(): DbUser? {
        return userRepository.getLoggedUser()
    }

    fun validate(): Boolean {

        loginBody.value.let {
            val email = it?.user_email
            val password = it?.user_password

            if (email.isNullOrEmpty()) {
                error.value = Response((getApplication() as MainApplication).getString(R.string.empty_email))
                return false
            }

            if (password.isNullOrEmpty()) {
                error.value = Response((getApplication() as MainApplication).getString(R.string.empty_password))
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