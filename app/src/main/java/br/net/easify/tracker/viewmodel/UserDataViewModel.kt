package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.interfaces.ILocalUser
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.api.UserService
import br.net.easify.tracker.repositories.database.model.SqliteUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class UserDataViewModel(application: Application) :
    AndroidViewModel(application), ILocalUser {
    private val disposable = CompositeDisposable()

    private var userService = UserService(application)

    var userData = MutableLiveData<SqliteUser>()
    var errorResponse = MutableLiveData<Response>()

    @Inject
    lateinit var userRepository: UserRepository

    init {
        (getApplication() as MainApplication)
            .getAppComponent()?.inject(this)
        
        userRepository.getLoggedUser()
    }

    fun update(user: SqliteUser) {
        disposable.add(
            userService.update(user.toUser())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    DisposableSingleObserver<Response>() {
                    override fun onSuccess(res: Response) {
                        userRepository.update(user)
                        errorResponse.value = Response("")
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        if (e is HttpException) {
                            if (e.code() == 401) {
                                errorResponse.value =
                                    Response(
                                        false,
                                        (getApplication() as MainApplication).getString(
                                            R.string.unauthorized
                                        )
                                    )
                            } else {
                                errorResponse.value =
                                    Response(false, e.response().message())
                            }
                        } else {
                            errorResponse.value =
                                Response(false, e.message.toString())
                        }
                    }
                })
        )
    }

    override fun getLoggedUser() {
        userData.value = userRepository.getLoggedUser()
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}