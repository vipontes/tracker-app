package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.DbRoute
import javax.inject.Inject

class RouteRepository (application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun getRoute(userRouteId: Long): DbRoute? = database.routeDao().getRoute(userRouteId)

    fun getAll(): List<DbRoute>? = database.routeDao().getAll()

    fun insert(dbRoute: DbRoute): Long = database.routeDao().insert(dbRoute)

    fun delete(userRouteId: Long) = database.routeDao().delete(userRouteId)

    fun delete() = database.routeDao().delete()

    fun update(dbRoute: DbRoute): Int = database.routeDao().update(dbRoute)
}