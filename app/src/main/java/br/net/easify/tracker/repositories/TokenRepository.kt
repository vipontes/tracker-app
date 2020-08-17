package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.DbToken
import javax.inject.Inject

class TokenRepository  (application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun get(): DbToken? = database.tokenDao().get()

    fun insert(dbToken: DbToken) = database.tokenDao().insert(dbToken)

    fun delete() = database.tokenDao().delete()
}