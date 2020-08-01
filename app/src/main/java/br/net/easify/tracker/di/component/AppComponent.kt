package br.net.easify.tracker.di.component

import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.di.module.AppModule
import br.net.easify.tracker.di.module.DatabaseModule
import br.net.easify.tracker.di.module.PrefsModule
import br.net.easify.tracker.di.module.ServiceModule
import br.net.easify.tracker.viewmodel.HomeViewModel
import br.net.easify.tracker.viewmodel.LoginViewModel
import br.net.easify.tracker.viewmodel.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DatabaseModule::class, PrefsModule::class, ServiceModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun application(app: AppModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: MainApplication)
    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: MainViewModel)
    fun inject(viewModel: HomeViewModel)

}
