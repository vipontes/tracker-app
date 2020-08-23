package br.net.easify.tracker.repositories.api

import android.app.Application
import br.net.easify.tracker.repositories.api.interfaces.IUser
import br.net.easify.tracker.model.User
import br.net.easify.tracker.repositories.api.interceptor.AuthInterceptor
import br.net.easify.tracker.helpers.Constants
import br.net.easify.tracker.model.Response
import io.reactivex.Single
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body

class UserService (application: Application) {

    private val api = RetrofitBuilder(application).retrofitAuth()
        .create(IUser::class.java)

    fun getUser(userId: Int): Single<User> {
        return api.getUser(userId)
    }

    fun update(user: User): Single<Response> {
        return api.update(user)
    }
}