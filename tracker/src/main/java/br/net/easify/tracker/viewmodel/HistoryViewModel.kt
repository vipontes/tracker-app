package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.database.model.SqliteRoute
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.helpers.Formatter
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.Route
import br.net.easify.tracker.repositories.RouteRepository
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.api.RouteService
import br.net.easify.tracker.helpers.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class HistoryViewModel (application: Application) : AndroidViewModel(application) {

    private val disposable = CompositeDisposable()

    val totalDistance by lazy { MutableLiveData<String>() }
    val routes by lazy { MutableLiveData<List<SqliteRoute>>() }
    val toastMessage by lazy { MutableLiveData<Response>() }
    val deleteRouteResponse by lazy { MutableLiveData<Response>() }

    private var routeService = RouteService(application)

    @Inject
    lateinit var routeRepository: RouteRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var prefs: SharedPreferencesHelper

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun refresh() {
        getAll()
    }

    fun refreshApi() {
        getRouteFromApi()
    }

    private fun getAll() {
        val localRoutes = routeRepository.getAll()
        if (localRoutes != null && localRoutes.isNotEmpty()) {
            routes.value = localRoutes
        } else {
            getRouteFromApi()
        }
    }

    fun deleteRoute(routeId: Long) {
        disposable.add(
            routeService.deleteRoute(routeId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    DisposableSingleObserver<Response>() {
                    override fun onSuccess(res: Response) {
                        routeRepository.delete(routeId)
                        getAll()
                        deleteRouteResponse.value = res
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        deleteRouteResponse.value =
                            Response(
                                false,
                                (getApplication() as MainApplication).getString(
                                    R.string.delete_route_error
                                )
                            )
                    }
                })
        )
    }

    fun fromRoute(route: Route): SqliteRoute {

        var res = SqliteRoute(0, 1)

        res.user_route_id = route.userRouteId
        res.user_id = route.userId
        res.user_route_duration = route.userRouteDuration
        res.user_route_distance = route.userRouteDistance
        res.user_route_calories = route.userRouteCalories
        res.user_route_rhythm = route.userRouteRhythm
        res.user_route_speed = route.userRouteSpeed
        res.user_route_description = route.userRouteDescription
        res.user_route_start_time = route.userRouteStartTime
        res.user_route_end_time = route.userRouteEndTime

        return res
    }

    private fun getRouteFromApi() {
        prefs.getCurrentRoute().let {
            if (it.isEmpty()) {
                routeRepository.delete()

                val userId = userRepository.getLoggedUser()?.user_Id!!
                disposable.add(
                    routeService.getRoutesByUser(userId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object :
                            DisposableSingleObserver<List<Route>>() {
                            override fun onSuccess(res: List<Route>) {

                                var routesFromApi: ArrayList<SqliteRoute> =
                                    arrayListOf()
                                for (item in res) {
                                    val route = fromRoute(item)
                                    routeRepository.insert(route)
                                    routesFromApi.add(route)
                                }

                                routes.value = routesFromApi
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()

                                if (e is HttpException) {
                                    if (e.code() == 401) {
                                        toastMessage.value =
                                            Response(
                                                false,
                                                (getApplication() as MainApplication).getString(
                                                    R.string.unauthorized
                                                )
                                            )
                                    } else {
                                        toastMessage.value =
                                            Response(
                                                false,
                                                (getApplication() as MainApplication).getString(
                                                    R.string.internal_error
                                                )
                                            )
                                    }
                                } else {
                                    toastMessage.value =
                                        Response(
                                            false,
                                            (getApplication() as MainApplication).getString(
                                                R.string.internal_error
                                            )
                                        )
                                }
                            }
                        })
                )
            }
        }
    }

    fun getTotalDistance() {
        val routesFromDb = routes.value
        routesFromDb?.let {
            val summarized = summarizeDistance(it)
            totalDistance.value = Formatter.decimalFormatterTwoDigits(summarized)
        }
    }

    private fun summarizeDistance(routes: List<SqliteRoute>): Double {
        var distance = 0.0;
        for (item in routes) {
            item.user_route_distance?.let {
                val itemDistance = it
                    .replace(",", ".")
                    .toDouble()
                distance += itemDistance
            }
        }
        return distance
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}