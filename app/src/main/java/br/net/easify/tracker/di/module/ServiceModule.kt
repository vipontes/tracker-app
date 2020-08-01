package br.net.easify.tracker.di.module

import android.app.Application
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.utils.ServiceHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule {
    @Provides
    @Singleton
    fun providesServiceHelper(application: Application): ServiceHelper {
        return ServiceHelper(application)
    }
}