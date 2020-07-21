package br.net.easify.tracker.di.module

import br.net.easify.tracker.interfaces.ILogin
import br.net.easify.tracker.services.LoginService
import br.net.easify.tracker.utils.Constants
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApiModule {

    @Provides
    fun provideLogin(): ILogin {
        return Retrofit.Builder()
            .baseUrl(Constants.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ILogin::class.java)
    }

    @Provides
    fun provideLoginService(): LoginService {
        return LoginService()
    }
}