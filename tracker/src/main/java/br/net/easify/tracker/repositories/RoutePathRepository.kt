package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.database.AppDatabase
import br.net.easify.database.model.SqliteRoutePath
import br.net.easify.tracker.MainApplication
import javax.inject.Inject

class RoutePathRepository (application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun getRoutePath(userRoutePathId: Long): SqliteRoutePath? = database.routePathDao().getRoutePath(userRoutePathId)

    fun getPathFromRoute(userRouteId: Long): List<SqliteRoutePath> = database.routePathDao().getPathFromRoute(userRouteId)

    fun insert(sqliteRoutePath: SqliteRoutePath): Long = database.routePathDao().insert(sqliteRoutePath)

    fun insert(collection: List<SqliteRoutePath>) = database.routePathDao().insert(collection)

    fun delete(userRoutePathId: Long) = database.routePathDao().delete(userRoutePathId)

    fun delete() = database.routePathDao().delete()

    fun update(sqliteRoutePath: SqliteRoutePath) = database.routePathDao().update(sqliteRoutePath)
}