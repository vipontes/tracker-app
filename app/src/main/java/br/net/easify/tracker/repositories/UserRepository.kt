package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.DbUser
import javax.inject.Inject

class UserRepository(application: Application) : AndroidViewModel(application) {

    private val userData = MutableLiveData<DbUser>()

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
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
}