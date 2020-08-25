package br.net.easify.tracker.repositories.api

import android.app.Application
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.User
import br.net.easify.tracker.repositories.api.interfaces.IUser
import io.reactivex.Single

class UserService (application: Application) {

    private val api = RetrofitBuilder(application).retrofitAuth()
        .create(IUser::class.java)

    fun getUser(userId: Int): Single<User> {
        return api.getUser(userId)
    }

    fun update(data: User): Single<Response> {
        return api.update(data)
    }
}