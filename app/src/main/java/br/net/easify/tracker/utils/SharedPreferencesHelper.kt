package br.net.easify.great.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedPreferencesHelper(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    private val isFirstTimeLaunch = "IsFirstTimeLaunch"
    private val clienteId = "clienteId"
    private val token = "token"

    fun setClienteId(key: Int) {
        prefs.edit().putInt(clienteId, key).commit()
    }

    fun getClienteId() = prefs.getInt(clienteId, 0)
    fun removeClienteId() = prefs.edit().remove(clienteId).commit()

    fun setWelcomeShown() {

        var editor = prefs.edit()

        editor.putBoolean(isFirstTimeLaunch, false)
        editor.commit()
    }

    fun isFirstTimeLaunch(): Boolean {
        return prefs.getBoolean(isFirstTimeLaunch, true)
    }

    fun setToken(newToken: String) {
        prefs.edit().putString(token, newToken).apply()
    }

    fun getToken():String {
        if (prefs != null) {
            return prefs.getString(token, "")!!
        } else {
            return ""
        }
    }

}