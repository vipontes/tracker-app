package br.net.easify.tracker.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.helpers.ServiceHelper
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var serviceHelper: ServiceHelper

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun startLocationService() {
        val gpsService = LocationService()
        val gpsIntent = Intent(getApplication(), gpsService::class.java)
        if (!serviceHelper.isMyServiceRunning(gpsService::class.java)) {
            (getApplication() as MainApplication).startService(gpsIntent)
        }
    }
}