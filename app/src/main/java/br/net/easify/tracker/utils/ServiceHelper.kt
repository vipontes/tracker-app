package br.net.easify.tracker.utils

import android.app.ActivityManager
import android.content.Context

class ServiceHelper (private val applicationContext: Context) {
    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}