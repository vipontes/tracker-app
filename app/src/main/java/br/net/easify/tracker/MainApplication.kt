package br.net.easify.tracker

import android.app.Application
import br.net.easify.tracker.di.AppInjector

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }
}