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

    val totalDistance by lazy { MutableLiveData<String>() }

    @Inject
    lateinit var routeRepository: RouteRepository

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun refresh() {
        routeRepository.getAll()
    }

    fun getTotalDistance() {
        val routesFromDb = routeRepository.routes.value
        routesFromDb?.let {
            val summarized = summarizeDistance(it)
            totalDistance.value = Formatter.decimalFormatterTwoDigits(summarized)
        }
    }

    private fun summarizeDistance(routes: List<DbRoute>): Double {
        var distance = 0.0;
        for (item in routes) {
            item.user_route_distance?.let {
                val itemDistance = it
                    .replace(",", ".")
                    .toDouble()
                distance += itemDistance
            }
        }
        return distance
    }
}