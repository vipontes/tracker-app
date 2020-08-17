package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.database.model.DbUser
import br.net.easify.tracker.repositories.RoutePathRepository
import br.net.easify.tracker.repositories.RouteRepository
import br.net.easify.tracker.repositories.TokenRepository
import br.net.easify.tracker.repositories.UserRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SettingsViewModel (application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    val userData by lazy { MutableLiveData<DbUser>() }

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var routeRepository: RouteRepository

    @Inject
    lateinit var routePathRepository: RoutePathRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        val loggedUser = userRepository.getLoggedUser()
        loggedUser?.let {
            userData.value = it
        }
    }

    fun logout() {
        userRepository.delete()
        routeRepository.delete()
        routePathRepository.delete()
        tokenRepository.delete()
        userData.value = null
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}