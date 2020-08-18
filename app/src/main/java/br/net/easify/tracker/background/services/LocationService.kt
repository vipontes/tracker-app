package br.net.easify.tracker.background.services

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.SqliteRoute
import br.net.easify.tracker.repositories.database.model.SqliteRoutePath
import br.net.easify.tracker.helpers.Constants
import br.net.easify.tracker.helpers.Formatter
import br.net.easify.tracker.helpers.SharedPreferencesHelper
import br.net.easify.tracker.view.main.MainActivity
import javax.inject.Inject


class LocationService : Service(), LocationListener {

    private val defaultLocationInterval = (4 * 1000).toLong() // Milliseconds
    private val defaultLocationDistance = 3f // Meters
    private lateinit var locationManager: LocationManager
    private var lastLocation: Location? = null

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var prefs: SharedPreferencesHelper

    companion object {
        val locationChangeAction = "br.net.easify.tracker.background.receivers.LocationBroadcastReceiver"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startForegroundService()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        (application as MainApplication).getAppComponent()?.inject(this)
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isCostAllowed = true
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false

        try {
            locationManager.requestLocationUpdates(defaultLocationInterval, defaultLocationDistance, criteria, this, null)
        } catch (ex: SecurityException) {
        } catch (ex: IllegalArgumentException) {
        }


//        try {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, defaultLocationInterval, defaultLocationDistance, this)
//        } catch (ex: SecurityException) {
//        } catch (ex: IllegalArgumentException) {
//        }

//        try {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, defaultLocationInterval, defaultLocationDistance, this)
//        } catch (ex: SecurityException) {
//        } catch (ex: IllegalArgumentException) {
//        }
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForegroundService()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getCurrentRoute(): SqliteRoute? {
        var routeId: Long = 0
        prefs.getCurrentRoute().let {
            if (it.isNotEmpty()) {
                routeId = it.toLong()
            }
        }

        var route = database.routeDao().getRoute(routeId)
        route?.let {
            return it
        }

        return null
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {

            var previousLocation: Location? = lastLocation

            lastLocation = Location(it)

            if (previousLocation == null ||
                (it.latitude != previousLocation.latitude &&
                it.longitude != previousLocation.longitude)
            ) {
                val route = getCurrentRoute()
                route?.let { value: SqliteRoute ->
                    if (value.in_progress == 1) {
                        val currentTime = Formatter.currentDateTimeYMDAsString()
                        val routeId = value.user_route_id!!
                        val path = SqliteRoutePath(null, routeId, it.latitude, it.longitude, it.altitude, currentTime)
                        database.routePathDao().insert(path)
                    }
                }

                sendBroadcastData(it)
            }
        }
    }

    private fun sendBroadcastData(location: Location) {
        val intent = Intent(locationChangeAction)
        intent.putExtra(Constants.resultCode, Activity.RESULT_OK)
        intent.putExtra(Constants.provider, location.provider)
        intent.putExtra(Constants.altitude, location.altitude)
        intent.putExtra(Constants.latitude, location.latitude)
        intent.putExtra(Constants.longitude, location.longitude)

        val broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        broadcastManager.sendBroadcast(intent)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String?) {}
    override fun onProviderDisabled(provider: String?) {}

    private fun startForegroundService() {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification =
            NotificationCompat.Builder(this, Constants.channelId)
                .setContentTitle("Tracker")
                .setContentText("GPS Service")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_gps)
                .build()

        startForeground(100, notification)
    }

    private fun stopForegroundService() {
        stopForeground(true)
    }
}