package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.repositories.database.model.SqliteUser
import javax.inject.Inject

class UserDataViewModel (application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var userRepository: UserRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        userRepository.getLoggedUser()
    }

    fun update(user: SqliteUser) {
        userRepository.update(user)
    }

}