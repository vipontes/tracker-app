package br.net.easify.tracker.di.component

import br.net.easify.tracker.di.module.*
import br.net.easify.tracker.viewmodel.LoginViewModel
import br.net.easify.tracker.viewmodel.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        AppModule::class,
        AuthModule::class,
        UserModule::class,
        PrefsModule::class
    ]
)
interface ViewModuleComponent {
    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: MainViewModel)
}