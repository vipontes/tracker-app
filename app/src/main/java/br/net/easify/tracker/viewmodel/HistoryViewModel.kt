package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.database.model.DbRoute
import br.net.easify.tracker.helpers.Formatter
import br.net.easify.tracker.repositories.RouteRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HistoryViewModel (application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    val routes by lazy { MutableLiveData<List<DbRoute>>() }
    val totalDistance by lazy { MutableLiveData<String>() }

    @Inject
    lateinit var routeRepository: RouteRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun refresh() {
        val localRoutes = routeRepository.getAll()
        localRoutes?.let {
            val summarized = summarizeDistance(it)
            totalDistance.value = Formatter.decimalFormatterTwoDigits(summarized)
        }

        routes.value = localRoutes
    }

    private fun summarizeDistance(routes: List<DbRoute>): Double {
        var distance = 0.0;
        for (item in routes) {
            val itemDistance = item.user_route_distance
                .replace(",", ".")
                .toDouble()
            distance += itemDistance
        }
        return distance
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}