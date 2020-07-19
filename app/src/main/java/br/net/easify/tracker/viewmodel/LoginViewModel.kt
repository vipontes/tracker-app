package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.di.component.DaggerDatabaseComponent
import br.net.easify.tracker.di.module.AppModule

import javax.inject.Inject

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var database: AppDatabase
    
    init {
        DaggerDatabaseComponent.builder()
            .appModule(AppModule(application))
            .build()
            .inject(this)
    }




}