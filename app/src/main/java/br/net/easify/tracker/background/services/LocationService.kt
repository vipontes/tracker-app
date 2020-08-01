package br.net.easify.tracker.background.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import br.net.easify.tracker.R
import br.net.easify.tracker.utils.Constants
import br.net.easify.tracker.view.main.MainActivity

class LocationService : Service(), LocationListener {

    private val defaultLocationInterval = (5 * 1000).toLong() // Milesegundos
    private val defaultLocationDistance = 5f // Metros
    private lateinit var locationManager: LocationManager
    private lateinit var lastLocation: Location

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startForegroundService()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, defaultLocationInterval, defaultLocationDistance, this)
        } catch (ex: SecurityException) {
        } catch (ex: IllegalArgumentException) {
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, defaultLocationInterval, defaultLocationDistance, this)
        } catch (ex: SecurityException) {
        } catch (ex: IllegalArgumentException) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForegroundService()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            lastLocation = Location(it)
            Log.d("Location", lastLocation.toString())
        }
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