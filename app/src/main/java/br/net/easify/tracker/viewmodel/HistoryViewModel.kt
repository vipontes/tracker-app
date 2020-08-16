package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.database.AppDatabase
import br.net.easify.tracker.database.model.DbRoute
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HistoryViewModel (application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    val routes by lazy { MutableLiveData<List<DbRoute>>() }

    @Inject
    lateinit var database: AppDatabase

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    fun refresh() {
        routes.value = database.routeDao().getAll()
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}