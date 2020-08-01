package br.net.easify.tracker.services

import br.net.easify.tracker.database.model.DbToken
import br.net.easify.tracker.interfaces.IUser
import br.net.easify.tracker.model.User
import br.net.easify.tracker.services.interceptor.AuthInterceptor
import br.net.easify.tracker.utils.Constants
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class UserService () {

    private var interceptor: AuthInterceptor = AuthInterceptor(DbToken("", ""))

    fun setTokens(tokens: DbToken) {
        this.interceptor.setTokens(tokens)
    }

    private val api = Retrofit.Builder()
        .baseUrl(Constants.apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(addHttpInterceptor())
        .build()
        .create(IUser::class.java)

    private fun addHttpInterceptor(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(this.interceptor)
        return okHttpClientBuilder.build()
    }


    fun getUser(userId: Int): Single<User> {
        return api.getUser(userId)
    }
}