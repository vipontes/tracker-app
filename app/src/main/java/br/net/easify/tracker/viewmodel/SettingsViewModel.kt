package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.database.model.DbUser
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SettingsViewModel (application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    val userData by lazy { MutableLiveData<DbUser>() }

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        val loggedUser = database.userDao().getLoggedUser()
        loggedUser?.let {
            userData.value = it
        }
    }

    fun logout() {
        database.userDao().delete()
        database.activityDao().delete()
        database.routeDao().delete()
        database.routePathDao().delete()
        database.tokenDao().delete()
        userData.value = null
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}