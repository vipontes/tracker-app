package br.net.easify.tracker.services

import br.net.easify.tracker.di.component.DaggerUserComponent
import br.net.easify.tracker.interfaces.IUser
import br.net.easify.tracker.model.User
import io.reactivex.Single
import javax.inject.Inject

class UserService {

    @Inject
    lateinit var api: IUser

    init {
        DaggerUserComponent.create().inject(this)
    }

    fun getUser(userId: Int): Single<User> {
        return api.getUser(userId)
    }
}