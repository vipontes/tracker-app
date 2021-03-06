package br.net.easify.tracker.di.module

import android.app.Application
import br.net.easify.tracker.helpers.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrefsModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferencesHelper {
        return SharedPreferencesHelper(application)
    }
}