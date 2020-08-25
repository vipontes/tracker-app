package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.SqliteToken
import br.net.easify.tracker.repositories.database.model.SqliteUser
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UserRepository(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun saveTokens(value: Token) {
        deleteToken()
        val tokenLocal = SqliteToken(value.token, value.refreshToken)
        insertToken(tokenLocal)
    }

    fun saveLoggedUser(user: User) {
        val dbUser = SqliteUser().fromUser(user)
        delete()
        insert(dbUser)
    }

    fun getLoggedUser(): SqliteUser? {
        return database.userDao().getLoggedUser()
    }

    fun insert(sqliteUser: SqliteUser): Long {
        return database.userDao().insert(sqliteUser)
    }

    fun delete(userId: Long) = database.userDao().delete(userId)

    fun delete() {
        database.userDao().delete()
    }

    fun logout() {
        runBlocking {
            database.routeDao().delete()
            database.routePathDao().delete()
            database.tokenDao().delete()
            database.userDao().delete()
        }
    }

    fun update(sqliteUser: SqliteUser) {
        database.userDao().update(sqliteUser)
    }

    fun getToken(): SqliteToken? = database.tokenDao().get()

    fun insertToken(sqliteToken: SqliteToken) =
        database.tokenDao().insert(sqliteToken)

    fun deleteToken() = database.tokenDao().delete()


}