package br.net.easify.tracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesHelper  @Inject constructor(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

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