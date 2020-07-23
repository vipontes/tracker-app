package br.net.easify.tracker.di.module

import br.net.easify.tracker.interfaces.ILogin
import br.net.easify.tracker.interfaces.IUser
import br.net.easify.tracker.services.interceptor.AuthInterceptor
import br.net.easify.tracker.utils.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor()
    }

    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(interceptor)
        return okHttpClientBuilder.build()
    }

    @Provides
    fun provideUserApi(client: OkHttpClient): IUser {
        return Retrofit.Builder()
            .baseUrl(Constants.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
            .create(IUser::class.java)
    }

    @Provides
    fun provideLoginApi(): ILogin {
        return Retrofit.Builder()
            .baseUrl(Constants.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ILogin::class.java)
    }
}