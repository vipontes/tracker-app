package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.database.model.SqliteUser
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.interfaces.ILocalUser
import br.net.easify.tracker.repositories.UserRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SettingsViewModel (application: Application) : AndroidViewModel(application), ILocalUser {
    private val disposable = CompositeDisposable()

    var userData = MutableLiveData<SqliteUser>()

    @Inject
    lateinit var userRepository: UserRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        getLoggedUser()
    }

    fun logout() {
        userRepository.logout()
    }

    override fun getLoggedUser() {
        userData.value = userRepository.getLoggedUser()
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}