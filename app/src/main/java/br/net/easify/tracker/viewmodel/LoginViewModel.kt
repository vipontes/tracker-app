package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.repositories.database.model.SqliteUser
import javax.inject.Inject

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val loginBody by lazy { MutableLiveData<LoginBody>() }
    val error by lazy { MutableLiveData<Response>() }

    @Inject
    lateinit var userRepository: UserRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        userRepository.getLoggedUser()
        loginBody.value = LoginBody("", "")
    }

    fun login() {
        loginBody.value.let {
            val email = it?.user_email.toString()
            val password = it?.user_password.toString()
            userRepository.checkLogin(email, password)
        }
    }

    fun getUserFromToken() = userRepository.getUserFromToken()

    fun getLoggedUser(): SqliteUser? {
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

}