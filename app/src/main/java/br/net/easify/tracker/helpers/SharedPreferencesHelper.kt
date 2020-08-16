package br.net.easify.tracker.helpers

import android.content.Context
import androidx.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesHelper @Inject constructor(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    private val currentRoute = "currentRoute"

    fun setCurrentRoute(value: String) {
        prefs.edit().putString(currentRoute, value).apply()
    }

    fun getCurrentRoute():String {
        return if (prefs != null) {
            prefs.getString(currentRoute, "")!!
        } else {
            ""
        }
    }

    fun removeCurrentRoute() {
        prefs.edit().remove(currentRoute).apply()
    }
}