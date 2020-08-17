package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.DbRoutePath
import javax.inject.Inject

class RoutePathRepository (application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun getRoutePath(userRoutePathId: Long): DbRoutePath? = database.routePathDao().getRoutePath(userRoutePathId)

    fun getPathFromRoute(userRouteId: Long): List<DbRoutePath> = database.routePathDao().getPathFromRoute(userRouteId)

    fun insert(dbRoutePath: DbRoutePath): Long = database.routePathDao().insert(dbRoutePath)

    fun delete(userRoutePathId: Long) = database.routePathDao().delete(userRoutePathId)

    fun delete() = database.routePathDao().delete()

    fun update(dbRoutePath: DbRoutePath) = database.routePathDao().update(dbRoutePath)
}