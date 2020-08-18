package br.net.easify.tracker.repositories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.Route
import br.net.easify.tracker.model.RoutePathPost
import br.net.easify.tracker.model.RoutePost
import br.net.easify.tracker.repositories.api.RouteService
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.SqliteRoute
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class RouteRepository (application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    val trackerRoute by lazy { MutableLiveData<SqliteRoute>() }
    val toastMessage by lazy { MutableLiveData<Response>() }
    val routes by lazy { MutableLiveData<List<SqliteRoute>>() }

    private var routeService = RouteService(application)

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun synchronizeTrackingActivity(route: SqliteRoute) {
        val userId = database.userDao().getLoggedUser()?.user_Id!!
        val data = getRoutePost(route, userId)

        disposable.add(
            routeService.postRoute(data)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Route>() {
                    override fun onSuccess(res: Route) {
                        route.sync = 1
                        update(route)
                        toastMessage.value =
                            Response((getApplication() as MainApplication).getString(R.string.tracking_successfully_saved))
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        if (e is HttpException) {
                            if (e.code() == 401) {
                                toastMessage.value =
                                    Response((getApplication() as MainApplication).getString(R.string.unauthorized))
                            } else {
                                toastMessage.value =
                                    Response((getApplication() as MainApplication).getString(R.string.internal_error))
                            }
                        } else {
                            toastMessage.value =
                                Response((getApplication() as MainApplication).getString(R.string.internal_error))
                        }
                    }
                })
        )
    }

    private fun getRoutePost(route: SqliteRoute, userId: Long): RoutePost {
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

    fun getAll() {
        var localRoutes = database.routeDao().getAll()
        if (localRoutes != null && localRoutes.isNotEmpty()) {
            routes.value = localRoutes
        } else {
            val userId = database.userDao().getLoggedUser()?.user_Id!!
            disposable.add(
                routeService.getRoutesByUser(userId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<Route>>() {
                        override fun onSuccess(res: List<Route>) {

                            var routesFromApi: ArrayList<SqliteRoute> = arrayListOf()
                            for (item in res) {
                                val route = SqliteRoute(0, 1).fromRoute(item)
                                insert(route)
                                routesFromApi.add(route)
                            }

                            routes.value = routesFromApi
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()

                            if (e is HttpException) {
                                if (e.code() == 401) {
                                    toastMessage.value =
                                        Response((getApplication() as MainApplication).getString(R.string.unauthorized))
                                } else {
                                    toastMessage.value =
                                        Response((getApplication() as MainApplication).getString(R.string.internal_error))
                                }
                            } else {
                                toastMessage.value =
                                    Response((getApplication() as MainApplication).getString(R.string.internal_error))
                            }
                        }
                    })
            )
        }
    }

    fun insert(sqliteRoute: SqliteRoute): Long = database.routeDao().insert(sqliteRoute)

    fun delete(userRouteId: Long) = database.routeDao().delete(userRouteId)

    fun delete() = database.routeDao().delete()

    fun update(sqliteRoute: SqliteRoute): Int = database.routeDao().update(sqliteRoute)

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}