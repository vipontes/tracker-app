package br.net.easify.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}