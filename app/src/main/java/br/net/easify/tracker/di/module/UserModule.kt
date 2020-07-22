package br.net.easify.tracker.di.module

import br.net.easify.tracker.interfaces.IUser
import br.net.easify.tracker.services.LoginService
import br.net.easify.tracker.services.UserService
import br.net.easify.tracker.utils.Constants
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class UserModule {

    @Provides
    fun provideUser(): IUser {

        return Retrofit.Builder()
            .baseUrl(Constants.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(IUser::class.java)
    }

    @Provides
    fun provideUserService(): UserService {
        return UserService()
    }
}