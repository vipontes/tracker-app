package br.net.easify.tracker.utils

import android.content.Context
import androidx.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesHelper @Inject constructor(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    private val currentActivity = "currentActivity"

    fun setCurrentActivity(value: String) {
        prefs.edit().putString(currentActivity, value).apply()
    }

    fun getCurrentActivity():String {
        return if (prefs != null) {
            prefs.getString(currentActivity, "")!!
        } else {
            ""
        }
    }

    fun removeCurrentActivity() {
        prefs.edit().remove(currentActivity).apply()
    }

//    private val token = "token"
//    private val refreshToken = "refreshToken"
//
//    fun setToken(newToken: String) {
//        prefs.edit().putString(token, newToken).apply()
//    }
//
//    fun getToken():String {
//        if (prefs != null) {
//            return prefs.getString(token, "")!!
//        } else {
//            return ""
//        }
//    }
//
//    fun setRefreshToken(newToken: String) {
//        prefs.edit().putString(refreshToken, newToken).apply()
//    }
//
//    fun getRefreshToken():String {
//        if (prefs != null) {
//            return prefs.getString(refreshToken, "")!!
//        } else {
//            return ""
//        }
//    }
}