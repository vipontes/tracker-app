package br.net.easify.tracker.background.services

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.net.easify.tracker.R
import br.net.easify.tracker.repositories.database.AppDatabase
import br.net.easify.tracker.repositories.database.model.DbRoute
import br.net.easify.tracker.repositories.database.model.DbRoutePath
import br.net.easify.tracker.helpers.Constants
import br.net.easify.tracker.helpers.Formatter
import br.net.easify.tracker.helpers.SharedPreferencesHelper
import br.net.easify.tracker.view.main.MainActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import javax.inject.Inject

/**
 * After some tests, I noticed that this implementation is not as good as LocationService one
 * I'm keeping this code here for future reference
 */
class FusedLocationService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private val updateInterval: Long = (4 * 1000)
    private val fastestUpdateInterval = updateInterval / 2
    private var context: Context? = null
    private lateinit var lastLocation: Location

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var prefs: SharedPreferencesHelper

    companion object {
        val locationChangeAction = "br.net.easify.tracker.background.receivers.LocationBroadcastReceiver"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        context = getApplicationContext();
        if (checkPermsion(context)) {
            setupLocationService(context!!);
        }

        startForegroundService()
        return START_STICKY
    }

    private fun setupLocationService(context: Context) {
        if (checkPlayServices()) {
            mGoogleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
            createLocationRequest()
        }
    }

    fun checkPermsion(context: Context?): Boolean {
        val MyVersion = Build.VERSION.SDK_INT
        return if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                false
            } else ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.interval = updateInterval
        mLocationRequest!!.fastestInterval = fastestUpdateInterval
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mGoogleApiClient!!.connect()
    }

    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        return result == ConnectionResult.SUCCESS
    }

    private fun startLocationUpdates() {
        if (mGoogleApiClient!!.isConnected) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        //(application as MainApplication).getAppComponent()?.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForegroundService()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getCurrentRoute(): DbRoute? {
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

    override fun onConnected(bundle: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            lastLocation = Location(it)

            val intent = Intent(locationChangeAction)
            intent.putExtra(Constants.resultCode, Activity.RESULT_OK)
            intent.putExtra(Constants.provider, lastLocation.provider)
            intent.putExtra(Constants.altitude, lastLocation.altitude)
            intent.putExtra(Constants.latitude, lastLocation.latitude)
            intent.putExtra(Constants.longitude, lastLocation.longitude)

            val route = getCurrentRoute()
            route?.let {
                if (route.in_progress == 1) {
                    val currentTime = Formatter.currentDateTimeYMDAsString()
                    val routeId = route.user_route_id!!
                    val path = DbRoutePath(null, routeId, lastLocation.latitude, lastLocation.longitude, lastLocation.altitude, currentTime)
                    database.routePathDao().insert(path)
                }
            }

            val broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
            broadcastManager.sendBroadcast(intent)
        }
    }
}