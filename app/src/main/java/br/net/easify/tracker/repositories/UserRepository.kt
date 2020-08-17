package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.DbUser
import javax.inject.Inject

class UserRepository (application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun getUser(userId: Long): DbUser? {
        return database.userDao().getUser(userId)
    }

    fun getLoggedUser(): DbUser? {
        return database.userDao().getLoggedUser()
    }

    fun getAll(): List<DbUser>? {
        return database.userDao().getAll()
    }

    fun insert(dbUser: DbUser): Long {
        return database.userDao().insert(dbUser)
    }

    fun delete(userId: Long) = database.userDao().delete(userId)

    fun delete()  = database.userDao().delete()

    fun update(dbUser: DbUser) = database.userDao().update(dbUser)
}