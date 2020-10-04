package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.database.AppDatabase
import br.net.easify.database.model.SqliteRoute
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.model.RoutePathPost
import br.net.easify.tracker.model.RoutePost
import javax.inject.Inject

class RouteRepository (application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun getRoutePost(route: SqliteRoute, userId: Long): RoutePost {
        val path = database.routePathDao().getPathFromRoute(route.user_route_id!!)!!

        val routePath = arrayListOf<RoutePathPost>()

        for (item in path) {
            routePath.add(
                RoutePathPost(
                    item.user_route_path_lat,
                    item.user_route_path_lng,
                    item.user_route_path_altitude,
                    item.user_route_path_datetime
                )
            )
        }

        return RoutePost(
            userId,
            route.user_route_duration,
            route.user_route_distance,
            route.user_route_calories,
            route.user_route_rhythm,
            route.user_route_speed,
            route.user_route_description,
            route.user_route_start_time,
            route.user_route_end_time,
            routePath
        )
    }

    fun getRoute(userRouteId: Long): SqliteRoute? = database.routeDao().getRoute(userRouteId)

    fun getAll(): List<SqliteRoute>? {
        return database.routeDao().getAll()
    }

    fun insert(sqliteRoute: SqliteRoute): Long = database.routeDao().insert(sqliteRoute)

    fun delete(userRouteId: Long) = database.routeDao().delete(userRouteId)

    fun delete() = database.routeDao().delete()

    fun update(sqliteRoute: SqliteRoute): Int = database.routeDao().update(sqliteRoute)
}