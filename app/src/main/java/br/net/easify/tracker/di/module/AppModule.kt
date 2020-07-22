package br.net.easify.tracker.di.module

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val application: Application) {

    @Provides
    fun providesApplication(): Application {
        return application
    }
}