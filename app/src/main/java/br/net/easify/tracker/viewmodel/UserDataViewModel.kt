package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.interfaces.ILocalUser
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.repositories.database.model.SqliteUser
import javax.inject.Inject

class UserDataViewModel (application: Application) : AndroidViewModel(application),
    ILocalUser {

    var userData = MutableLiveData<SqliteUser>()
    var errorResponse = MutableLiveData<Response>()

    @Inject
    lateinit var userRepository: UserRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
        userRepository.getLoggedUser()
    }

    fun update(user: SqliteUser) {
//        val updatedRecordCount = database.userDao().update(sqliteUser)
//        if (updatedRecordCount == 1) {
//            val user = sqliteUser.toUser()
//            disposable.add(
//                userService.update(user)
//                    .subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(object :
//                        DisposableSingleObserver<Response>() {
//                        override fun onSuccess(res: Response) {
//                            errorResponse.value = Response("")
//                        }
//
//                        override fun onError(e: Throwable) {
//                            e.printStackTrace()
//
//                            if (e is HttpException) {
//                                if (e.code() == 401) {
//                                    errorResponse.value =
//                                        Response(
//                                            false,
//                                            (getApplication() as MainApplication).getString(
//                                                R.string.unauthorized
//                                            )
//                                        )
//                                } else {
//                                    errorResponse.value =
//                                        Response(false, e.response().message())
//                                }
//                            } else {
//                                errorResponse.value =
//                                    Response(false, e.message.toString())
//                            }
//                        }
//                    })
//            )
//        } else {
//            errorResponse.value = Response(
//                false,
//                (getApplication() as MainApplication).getString(R.string.user_update_error)
//            )
//        }
    }

    override fun getLoggedUser() {
        userData.value = userRepository.getLoggedUser()
    }

}