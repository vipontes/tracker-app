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

    @Inject
    lateinit var userRepository: UserRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun getUserData(): MutableLiveData<DbUser> = userRepository.getUserData()

    fun logout() {
        userRepository.logout()
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}