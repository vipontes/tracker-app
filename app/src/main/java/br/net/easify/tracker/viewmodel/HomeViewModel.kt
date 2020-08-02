package br.net.easify.tracker.viewmodel

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.database.model.DbActivity
import br.net.easify.tracker.database.model.DbRoute
import br.net.easify.tracker.database.model.DbRoutePath
import br.net.easify.tracker.enums.TrackerActivityState
import br.net.easify.tracker.model.User
import br.net.easify.tracker.utils.Constants
import br.net.easify.tracker.utils.Formatter
import br.net.easify.tracker.utils.ServiceHelper
import br.net.easify.tracker.utils.SharedPreferencesHelper
import io.reactivex.disposables.CompositeDisposable
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    val errorMessage by lazy { MutableLiveData<String>() }
    val trackerActivityState by lazy { MutableLiveData<TrackerActivityState>() }
    val currentLocation by lazy { MutableLiveData<GeoPoint>() }
    val trackerActivity by lazy { MutableLiveData<DbActivity>() }

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

    init {
        trackerActivityState.value = TrackerActivityState.idle
        (getApplication() as MainApplication).getAppComponent()?.inject(this)

        val intentFilter = IntentFilter(LocationService.locationChangeAction)
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(onLocationServiceNotification, intentFilter)

        trackerActivity.value = DbActivity(
            0,
            0,
            getApplication<Application>().resources.getString(R.string.default_duration),
            getApplication<Application>().resources.getString(R.string.default_distance),
            getApplication<Application>().resources.getString(R.string.default_calories),
            getApplication<Application>().resources.getString(R.string.default_rhythm),
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
                val routeId = activity.user_route_id
                val routePathList = database.routePathDao().getPathFromRoute(routeId)
                if (routePathList.isNotEmpty()) {
                    val routePath = routePathList.last()
                    val latitude = routePath.user_route_path_lat
                    val longitude = routePath.user_route_path_lng
                    currentLocation.value = GeoPoint(latitude, longitude)
                    trackerActivityState.value = TrackerActivityState.started
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

    fun startTracker() {

        val startPoint = currentLocation.value

        startPoint?.let { pt: GeoPoint ->
            val userId = database.userDao().getLoggedUser()?.user_Id
            userId?.let {
                // 1 - Create a route
                val startTime = Formatter.currentDateTimeDMYAsString();
                val databaseFieldTime = Formatter.currentDateTimeYMDAsString();
                val description = "Activity $startTime"

                val route = DbRoute(0, userId, description, databaseFieldTime, null)
                val routeId = database.routeDao().insert(route)

                if (routeId > 0) {
                    // 2 - Create a path
                    val routePath = DbRoutePath(0, routeId, pt.latitude, pt.longitude, databaseFieldTime)
                    database.routePathDao().insert(routePath)

                    // 3 - Create an activity
                    val activity = DbActivity(
                        0,
                        routeId,
                        getApplication<Application>().resources.getString(R.string.default_duration),
                        getApplication<Application>().resources.getString(R.string.default_distance),
                        getApplication<Application>().resources.getString(R.string.default_calories),
                        getApplication<Application>().resources.getString(R.string.default_rhythm),
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
                } else {
                    // Report error
                    errorMessage.value = "Could not start activity."
                }
            }
        }
    }

    fun stopTracker() {
        var activityId: Long = 0
        prefs.getCurrentActivity().let {
            if (it.isNotEmpty()) {
                activityId = it.toLong()
            }
        }

        var activity = database.activityDao().getActivity(activityId)
        activity?.let {

            activity.in_progress = 0
            database.activityDao().update(activity)

            prefs.removeCurrentActivity()

            trackerActivityState.value = TrackerActivityState.idle
        }
    }

    fun getTrackerState(): TrackerActivityState? {
        return trackerActivityState.value
    }

    override fun onCleared() {
        super.onCleared()
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(onLocationServiceNotification)
        disposable.clear()
    }
}