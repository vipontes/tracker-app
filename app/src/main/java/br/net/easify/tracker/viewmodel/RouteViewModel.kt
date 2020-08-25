package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.R
import br.net.easify.tracker.api.RoutePathService
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.RoutePath
import br.net.easify.tracker.repositories.RoutePathRepository
import br.net.easify.tracker.repositories.RouteRepository
import br.net.easify.tracker.repositories.database.model.SqliteRoute
import br.net.easify.tracker.repositories.database.model.SqliteRoutePath
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class RouteViewModel (application: Application) :
    AndroidViewModel(application){
    private val disposable = CompositeDisposable()

    var route = MutableLiveData<SqliteRoute>()
    var routePath = MutableLiveData<List<SqliteRoutePath>>()
    val toastMessage by lazy { MutableLiveData<Response>() }

    private var routePathService = RoutePathService(application)

    @Inject
    lateinit var routeRepository: RouteRepository

    @Inject
    lateinit var routePathRepository: RoutePathRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun getRoute(routeId: Long) {
        route.value = routeRepository.getRoute(routeId)
    }

    fun getRoutePath(routeId: Long) {
        val path = routePathRepository.getPathFromRoute(routeId)
        if (path.isNotEmpty()) {
            routePath.value = path
        } else {
            disposable.add(
                routePathService.getPathFromRoute(routeId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<RoutePath>>() {
                        override fun onSuccess(res: List<RoutePath>) {
                            val data: ArrayList<SqliteRoutePath> = arrayListOf()

                            for (item in res)
                                data.add(SqliteRoutePath().fromRoutePath(item))

                            routePath.value = data
                            routePathRepository.insert(data)
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()

                            if (e is HttpException) {
                                if (e.code() == 401) {
                                    toastMessage.value =
                                        Response((getApplication() as MainApplication).getString(
                                            R.string.unauthorized))
                                } else {
                                    toastMessage.value = Response(false, e.message())
                                }
                            } else {
                                toastMessage.value = Response(false, e.toString())
                            }
                        }
                    })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }

}