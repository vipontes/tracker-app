package br.net.easify.tracker.viewmodel

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.background.services.TimerService
import br.net.easify.tracker.repositories.database.model.SqliteRoute
import br.net.easify.tracker.repositories.database.model.SqliteRoutePath
import br.net.easify.tracker.enums.TrackerActivityState
import br.net.easify.tracker.helpers.*
import br.net.easify.tracker.model.*
import br.net.easify.tracker.repositories.RoutePathRepository
import br.net.easify.tracker.repositories.RouteRepository
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.api.RouteService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.osmdroid.util.GeoPoint
import retrofit2.HttpException
import javax.inject.Inject

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val disposable = CompositeDisposable()

    val trackerRoute by lazy { MutableLiveData<SqliteRoute>() }
    val toastMessage by lazy { MutableLiveData<Response>() }
    val trackerActivityState by lazy { MutableLiveData<TrackerActivityState>() }
    val currentLocation by lazy { MutableLiveData<GeoPoint>() }

    private var routeService = RouteService(application)

    @Inject
    lateinit var serviceHelper: ServiceHelper

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var routeRepository: RouteRepository

    @Inject
    lateinit var routePathRepository: RoutePathRepository

    @Inject
    lateinit var prefs: SharedPreferencesHelper

    private val onLocationServiceNotification: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val resultCode = intent.getIntExtra(Constants.resultCode, Activity.RESULT_CANCELED)
            if (resultCode == Activity.RESULT_OK) {
                val latitude = intent.getDoubleExtra(Constants.latitude, 0.0)
                val longitude = intent.getDoubleExtra(Constants.longitude, 0.0)
                currentLocation.value = GeoPoint(latitude, longitude)
            }
        }
    }

    private val onTimerServiceNotification: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val resultCode = intent.getIntExtra(Constants.resultCode, Activity.RESULT_CANCELED)
            if (resultCode == Activity.RESULT_OK) {
                val route = trackerRoute.value as SqliteRoute
                route.let {
                    val elapsedTime = intent.getLongExtra(Constants.elapsedTime, 0)
                    val displayData = Formatter.hmsTimeFormatter(elapsedTime)
                    val path = routePathRepository.getPathFromRoute(route.user_route_id!!)
                    val loggedUser = userRepository.getLoggedUser()
                    var userWeight = Constants.defaultWeight
                    loggedUser?.let {
                        userWeight = it.user_weight
                    }

                    val rhythm = TrackerHelper.calculateAverageRhythmInMilisecondsPerKilometer(path)
                    val formattedRhythm = Formatter.msTimeFormatter(rhythm.toLong())
                    val distance = TrackerHelper.calculateDistanceInKilometers(path)
                    val calories = TrackerHelper.calculateCalories(userWeight, path)
                    val speed = TrackerHelper.calculateAverageSpeedInKmPerHour(path)

                    route.user_route_duration = displayData
                    route.user_route_rhythm = formattedRhythm
                    route.user_route_distance = Formatter.decimalFormatterTwoDigits(distance)
                    route.user_route_calories = Formatter.decimalFormatterOneDigit(calories)
                    route.user_route_speed = Formatter.decimalFormatterOneDigit(speed)

                    trackerRoute.value = route
                    routeRepository.update(route)
                }
            }
        }
    }

    init {
        trackerActivityState.value = TrackerActivityState.idle
        (getApplication() as MainApplication).getAppComponent()?.inject(this)

        val locationIntent = IntentFilter(LocationService.locationChangeAction)
        LocalBroadcastManager.getInstance(getApplication())
            .registerReceiver(onLocationServiceNotification, locationIntent)

        val timerIntent = IntentFilter(TimerService.timerServiceElapsedTimeChanged)
        LocalBroadcastManager.getInstance(getApplication())
            .registerReceiver(onTimerServiceNotification, timerIntent)

        trackerRoute.value = createEmptyRoute(0, "", "", 0)

        checkRunningActivity()
    }

    private fun checkRunningActivity() {

        var routeId: Long = 0
        prefs.getCurrentRoute().let {
            if (it.isNotEmpty()) {
                routeId = it.toLong()
            }
        }

        val route = routeRepository.getRoute(routeId)
        route?.let {

            if (route.in_progress == 1) {
                trackerActivityState.value = TrackerActivityState.started
                val routeId = route.user_route_id!!
                val routePathList = routePathRepository.getPathFromRoute(routeId)
                if (routePathList.isNotEmpty()) {
                    val routePath = routePathList.last()
                    val latitude = routePath.user_route_path_lat
                    val longitude = routePath.user_route_path_lng
                    currentLocation.value = GeoPoint(latitude, longitude)
                }
            }
        }
    }

    fun startLocationService() {
        val gpsService = LocationService()
        val gpsIntent = Intent(getApplication(), gpsService::class.java)
        if (!serviceHelper.isMyServiceRunning(gpsService::class.java)) {
            (getApplication() as MainApplication).startService(gpsIntent)
        }
    }

    fun stopLocationService() {
        val gpsService = LocationService()
        val gpsIntent = Intent(getApplication(), gpsService::class.java)
        if (serviceHelper.isMyServiceRunning(gpsService::class.java)) {
            (getApplication() as MainApplication).stopService(gpsIntent)
        }
    }

    private fun startTimerService() {
        val intent = Intent(getApplication(), TimerService::class.java)
        if (!serviceHelper.isMyServiceRunning(TimerService::class.java)) {
            (getApplication() as MainApplication).startService(intent)
        }
    }

    private fun stopTimerService() {
        val intent = Intent(getApplication(), TimerService::class.java)
        if (serviceHelper.isMyServiceRunning(TimerService::class.java)) {
            (getApplication() as MainApplication).stopService(intent)
        }
    }

    fun startTracker() {

        val startPoint = currentLocation.value

        startPoint?.let { pt: GeoPoint ->
            val userId = userRepository.getLoggedUser()?.user_Id
            userId?.let {
                val startTime = Formatter.currentDateTimeDMYAsString();
                val databaseFieldTime = Formatter.currentDateTimeYMDAsString();
                val description = "Activity $startTime"

                val route = createEmptyRoute(userId, description, databaseFieldTime, 1)
                val routeId = routeRepository.insert(route)

                if (routeId > 0) {
                    route.user_route_id = routeId
                    routeRepository.update(route)
                    val routePath = SqliteRoutePath(null, routeId, pt.latitude, pt.longitude, pt.altitude, databaseFieldTime)
                    routePathRepository.insert(routePath)
                    prefs.setCurrentRoute(routeId.toString())
                    trackerActivityState.value = TrackerActivityState.started
                    trackerRoute.value = route
                    startTimerService()
                } else {
                    toastMessage.value =
                        Response((getApplication() as MainApplication).getString(R.string.start_activity_error))
                }
            }
        }
    }

    private fun createEmptyRoute(
        userId: Long,
        description: String,
        databaseFieldTime: String,
        inProgress: Int
    ): SqliteRoute {
        return SqliteRoute(
            null,
            userId,
            getApplication<Application>().resources.getString(R.string.default_duration),
            getApplication<Application>().resources.getString(R.string.default_distance),
            getApplication<Application>().resources.getString(R.string.default_calories),
            getApplication<Application>().resources.getString(R.string.default_rhythm),
            getApplication<Application>().resources.getString(R.string.default_speed),
            description,
            databaseFieldTime,
            null,
            inProgress,
            0
        )
    }

    fun stopTracker() {
        val route = getCurrentRoute()
        route?.let {
            val stopDatetime = Formatter.currentDateTimeYMDAsString();
            finishTrackerActivity(route, stopDatetime)
            if ( finishUserRoute(route, stopDatetime) ) {
                stopTimerService()
                synchronizeTrackingActivity(route)
                sendNotificationToHomeFragment(route)
            } else {
                toastMessage.value =
                    Response((getApplication() as MainApplication).getString(R.string.stopping_tracking_error))
            }
        }
    }

    private fun sendNotificationToHomeFragment(route: SqliteRoute) {
        prefs.removeCurrentRoute()
        trackerActivityState.value = TrackerActivityState.idle
        trackerRoute.value = createEmptyRoute(0, "", "", 0)
    }

    private fun finishTrackerActivity(route: SqliteRoute, stopDatetime: String) {
        route.in_progress = 0
        routeRepository.update(route)
    }

    private fun finishUserRoute(route: SqliteRoute, stopDatetime: String): Boolean {
        route.user_route_end_time = stopDatetime
        return routeRepository.update(route) > 0
    }

    fun getTrackerState(): TrackerActivityState? {
        return trackerActivityState.value
    }

    private fun getCurrentRoute(): SqliteRoute? {
        var routeId: Long = 0
        prefs.getCurrentRoute().let {
            if (it.isNotEmpty()) {
                routeId = it.toLong()
            }
        }

        val route = routeRepository.getRoute(routeId)
        route?.let {
            return it
        }

        return null
    }

    fun getCurrentTrackerPath() : ArrayList<GeoPoint> {
        var path: ArrayList<GeoPoint> = arrayListOf()

        val route = getCurrentRoute()
        route?.let {
            val routeId = route.user_route_id!!
            val routePath = routePathRepository.getPathFromRoute(routeId)
            path = MapHelper().geoPointFromRoutePath(routePath)
        }

        return path
    }

    private fun synchronizeTrackingActivity(route: SqliteRoute) {
        val userId = userRepository.getLoggedUser()?.user_Id!!
        val data = routeRepository.getRoutePost(route, userId)

        disposable.add(
            routeService.postRoute(data)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Route>() {
                    override fun onSuccess(res: Route) {
                        route.sync = 1
                        routeRepository.update(route)
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
                                toastMessage.value = Response(false, e.message())
                            }
                        } else {
                            toastMessage.value = Response(false, e.toString())
                        }
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()

        LocalBroadcastManager.getInstance(getApplication())
            .unregisterReceiver(onLocationServiceNotification)

        LocalBroadcastManager.getInstance(getApplication())
            .unregisterReceiver(onTimerServiceNotification)

        disposable.clear()
    }
}