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
import br.net.easify.tracker.api.RouteService
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.background.services.TimerService
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.database.model.DbActivity
import br.net.easify.tracker.database.model.DbRoute
import br.net.easify.tracker.database.model.DbRoutePath
import br.net.easify.tracker.enums.TrackerActivityState
import br.net.easify.tracker.helpers.*
import br.net.easify.tracker.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.osmdroid.util.GeoPoint
import retrofit2.HttpException
import javax.inject.Inject

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    val trackerActivityState by lazy { MutableLiveData<TrackerActivityState>() }
    val currentLocation by lazy { MutableLiveData<GeoPoint>() }
    val trackerActivity by lazy { MutableLiveData<DbActivity>() }
    val toastMessage by lazy { MutableLiveData<Response>() }

    private var routeService = RouteService(application)

    @Inject
    lateinit var serviceHelper: ServiceHelper

    @Inject
    lateinit var database: AppDatabase

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
                var activity = trackerActivity.value
                activity?.let {
                    val elapsedTime = intent.getLongExtra(Constants.elapsedTime, 0)
                    val displayData = Formatter.hmsTimeFormatter(elapsedTime)
                    val path = database.routePathDao().getPathFromRoute(activity.user_route_id)
                    val loggedUser = database.userDao().getLoggedUser()
                    var userWeight = Constants.defaultWeight
                    loggedUser?.let {
                        userWeight = it.user_weight
                    }

                    val rhythm = TrackerHelper.calculateAverageRhythmInMilisecondsPerKilometer(path)
                    val formattedRhythm = Formatter.msTimeFormatter(rhythm.toLong())
                    val distance = TrackerHelper.calculateDistanceInKilometers(path)
                    val calories = TrackerHelper.calculateCalories(userWeight, path)
                    val speed = TrackerHelper.calculateAverageSpeedInKmPerHour(path)

                    activity.duration = displayData
                    activity.rhythm = formattedRhythm
                    activity.distance = Formatter.decimalFormatterTwoDigits(distance)
                    activity.calories = Formatter.decimalFormatterOneDigit(calories)
                    activity.speed = Formatter.decimalFormatterOneDigit(speed)

                    trackerActivity.value = activity
                    database.activityDao().update(activity)
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

        trackerActivity.value = DbActivity(
            0,
            0,
            getApplication<Application>().resources.getString(R.string.default_duration),
            getApplication<Application>().resources.getString(R.string.default_distance),
            getApplication<Application>().resources.getString(R.string.default_calories),
            getApplication<Application>().resources.getString(R.string.default_rhythm),
            getApplication<Application>().resources.getString(R.string.default_speed),
            0,
            0,
            "",
            null
        )

        checkRunningActivity()
    }

    private fun checkRunningActivity() {

        var activityId: Long = 0
        prefs.getCurrentActivity().let {
            if (it.isNotEmpty()) {
                activityId = it.toLong()
            }
        }

        val activity = database.activityDao().getActivity(activityId)
        activity?.let {

            if (activity.in_progress == 1) {
                trackerActivityState.value = TrackerActivityState.started
                val routeId = activity.user_route_id
                val routePathList = database.routePathDao().getPathFromRoute(routeId)
                if (routePathList.isNotEmpty()) {
                    val routePath = routePathList.last()
                    val latitude = routePath.user_route_path_lat
                    val longitude = routePath.user_route_path_lng
                    currentLocation.value = GeoPoint(latitude, longitude)
                }
            }
        }
    }

    fun startLocationService(): Boolean {
        val gpsService = LocationService()
        val gpsIntent = Intent(getApplication(), gpsService::class.java)
        if (!serviceHelper.isMyServiceRunning(gpsService::class.java)) {
            (getApplication() as MainApplication).startService(gpsIntent)
        }

        return true
    }

    fun stopLocationService(): Boolean {
        val gpsService = LocationService()
        val gpsIntent = Intent(getApplication(), gpsService::class.java)
        if (serviceHelper.isMyServiceRunning(gpsService::class.java)) {
            (getApplication() as MainApplication).stopService(gpsIntent)
            return true
        }

        return true
    }

    fun startTimerService() {
        val intent = Intent(getApplication(), TimerService::class.java)
        if (!serviceHelper.isMyServiceRunning(TimerService::class.java)) {
            (getApplication() as MainApplication).startService(intent)
        }
    }

    fun stopTimerService() {
        val intent = Intent(getApplication(), TimerService::class.java)
        if (serviceHelper.isMyServiceRunning(TimerService::class.java)) {
            (getApplication() as MainApplication).stopService(intent)
        }
    }

    fun startTracker() {

        val startPoint = currentLocation.value

        startPoint?.let { pt: GeoPoint ->
            val userId = database.userDao().getLoggedUser()?.user_Id
            userId?.let {
                // 1 - Create a route
                val startTime = Formatter.currentDateTimeDMYAsString();
                val databaseFieldTime = Formatter.currentDateTimeYMDAsString();
                val description = "Activity $startTime"

                val route = DbRoute(null, userId, description, databaseFieldTime, null)
                val routeId = database.routeDao().insert(route)

                if (routeId > 0) {
                    // 2 - Create a path
                    val routePath = DbRoutePath(null, routeId, pt.latitude, pt.longitude, pt.altitude, databaseFieldTime)
                    database.routePathDao().insert(routePath)

                    // 3 - Create an activity
                    val activity = DbActivity(
                        null,
                        routeId,
                        getApplication<Application>().resources.getString(R.string.default_duration),
                        getApplication<Application>().resources.getString(R.string.default_distance),
                        getApplication<Application>().resources.getString(R.string.default_calories),
                        getApplication<Application>().resources.getString(R.string.default_rhythm),
                        getApplication<Application>().resources.getString(R.string.default_speed),
                        1,
                        0,
                        databaseFieldTime,
                        null
                    )
                    val activityId = database.activityDao().insert(activity)

                    // 4 - Save activityId to preferences
                    prefs.setCurrentActivity(activityId.toString())

                    // 5 - Report tracker state
                    trackerActivityState.value = TrackerActivityState.started

                    trackerActivity.value = activity

                    startTimerService()
                } else {
                    toastMessage.value =
                        Response((getApplication() as MainApplication).getString(R.string.start_activity_error))
                }
            }
        }
    }

    fun stopTracker() {
        val activity = getCurrentActivity()
        activity?.let {
            val stopDatetime = Formatter.currentDateTimeYMDAsString();
            finishTrackerActivity(activity, stopDatetime)
            if ( finishUserRoute(activity, stopDatetime) ) {
                sendNotificationToHomeFragment(activity)
                stopTimerService()
                synchronizeTrackingActivity(activity)
            } else {
                toastMessage.value =
                    Response((getApplication() as MainApplication).getString(R.string.stopping_tracking_error))
            }
        }
    }

    private fun synchronizeTrackingActivity(activity: DbActivity) {
        val userId = database.userDao().getLoggedUser()?.user_Id!!
        val route = database.routeDao().getRoute(activity.user_route_id)!!
        val data = getRoutePost(route, userId)

        disposable.add(
            routeService.postRoute(data)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Route>() {
                    override fun onSuccess(res: Route) {
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

    private fun getRoutePost(route: DbRoute, userId: Long): RoutePost {
        val path = database.routePathDao().getPathFromRoute(route.user_route_id!!)!!

        var routePath = arrayListOf<RoutePathPost>()

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

        val data = RoutePost(
            userId,
            route.user_route_description,
            route.user_route_start_time,
            route.user_route_end_time,
            routePath
        )
        return data
    }

    private fun sendNotificationToHomeFragment(activity: DbActivity?) {
        prefs.removeCurrentActivity()
        trackerActivityState.value = TrackerActivityState.idle
        trackerActivity.value = activity
    }

    private fun finishTrackerActivity(activity: DbActivity, stopDatetime: String) {
        activity.in_progress = 0
        activity.finished_at = stopDatetime
        database.activityDao().update(activity)
    }

    private fun finishUserRoute(activity: DbActivity, stopDatetime: String): Boolean {
        val routeId = activity.user_route_id
        val route = database.routeDao().getRoute(routeId)
        route?.let {
            it.user_route_end_time = stopDatetime
            return database.routeDao().update(it) > 0
        }

        return false
    }

    fun getTrackerState(): TrackerActivityState? {
        return trackerActivityState.value
    }

    private fun getCurrentActivity(): DbActivity? {
        var activityId: Long = 0
        prefs.getCurrentActivity().let {
            if (it.isNotEmpty()) {
                activityId = it.toLong()
            }
        }

        val activity = database.activityDao().getActivity(activityId)
        activity?.let {
            return it
        }

        return null
    }

    fun getCurrentTrackerPath() : ArrayList<GeoPoint> {
        val path: ArrayList<GeoPoint> = arrayListOf()

        val activity = getCurrentActivity()
        activity?.let {
            val routeId = activity.user_route_id
            val routePath = database.routePathDao().getPathFromRoute(routeId)
            for (point in routePath) {
                path.add(GeoPoint(point.user_route_path_lat, point.user_route_path_lng))
            }
        }

        return path
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(onLocationServiceNotification)
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(onTimerServiceNotification)
        disposable.clear()
    }
}