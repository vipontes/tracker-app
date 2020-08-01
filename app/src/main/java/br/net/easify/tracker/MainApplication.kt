package br.net.easify.tracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import br.net.easify.tracker.di.component.AppComponent
import br.net.easify.tracker.di.component.DaggerAppComponent
import br.net.easify.tracker.di.module.AppModule
import br.net.easify.tracker.utils.Constants

class MainApplication : Application() {

    private lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        this.appComponent = DaggerAppComponent.builder()
            .application(AppModule(this))
            .build()
    }

    fun getAppComponent(): AppComponent? {
        return appComponent
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChanel = NotificationChannel(
                Constants.channelId,
                "Tracker channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChanel)
        }
    }
}