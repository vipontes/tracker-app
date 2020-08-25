package br.net.easify.tracker.api

import android.app.Application
import br.net.easify.tracker.helpers.Constants
import br.net.easify.tracker.api.interceptor.AuthInterceptor
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder (var application: Application) {

    fun retrofitAuth(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(addHttpInterceptor())
            .build()
    }

    fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun addHttpInterceptor(): OkHttpClient {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.dispatcher(dispatcher)
        okHttpClientBuilder.addInterceptor(AuthInterceptor(application, this))
        return okHttpClientBuilder.build()
    }
}