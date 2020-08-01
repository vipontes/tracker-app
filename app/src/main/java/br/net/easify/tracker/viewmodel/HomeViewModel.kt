package br.net.easify.tracker.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.enums.TrackerActivityState
import br.net.easify.tracker.model.User
import br.net.easify.tracker.utils.ServiceHelper
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    val isActivityStarted by lazy { MutableLiveData<Boolean>() }
    val trackerActivityState by lazy { MutableLiveData<TrackerActivityState>() }

    @Inject
    lateinit var serviceHelper: ServiceHelper

    @Inject
    lateinit var database: AppDatabase

    init {
        isActivityStarted.value = false
        trackerActivityState.value = TrackerActivityState.idle
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun startLocationService(tracker: Boolean = false) {
        val gpsService = LocationService()
        val gpsIntent = Intent(getApplication(), gpsService::class.java)
        if (!serviceHelper.isMyServiceRunning(gpsService::class.java)) {
            (getApplication() as MainApplication).startService(gpsIntent)
            if (tracker) {
                trackerActivityState.value = TrackerActivityState.started
            }
        }
    }

    fun stopLocationService() {
        val gpsService = LocationService()
        val gpsIntent = Intent(getApplication(), gpsService::class.java)
        if (serviceHelper.isMyServiceRunning(gpsService::class.java)) {
            (getApplication() as MainApplication).stopService(gpsIntent)
            trackerActivityState.value = TrackerActivityState.idle
        }
    }

    fun getTrackerActivityState(): TrackerActivityState? {
        return trackerActivityState.value
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}