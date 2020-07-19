package br.net.easify.tracker.di

import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.di.component.DaggerAppComponent
import br.net.easify.tracker.di.module.AppModule

class AppInjector {
    companion object{
        fun init(mainApplication: MainApplication) {
            DaggerAppComponent.builder()
                .application(AppModule(mainApplication))
                .build()!!
                .inject(mainApplication)
        }
    }
}