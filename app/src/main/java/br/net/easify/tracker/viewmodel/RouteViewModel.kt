package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.net.easify.tracker.MainApplication
import io.reactivex.disposables.CompositeDisposable

class RouteViewModel (application: Application) :
    AndroidViewModel(application){
    private val disposable = CompositeDisposable()

    init {
        (getApplication() as MainApplication).getAppComponent()?.inject(this)
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}