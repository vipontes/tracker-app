package br.net.easify.tracker.di.component

import br.net.easify.tracker.di.module.AppModule
import br.net.easify.tracker.di.module.DatabaseModule
import br.net.easify.tracker.viewmodel.LoginViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        AppModule::class
    ]
)
interface DatabaseComponent {
    fun inject(viewModel: LoginViewModel)
}