package br.net.easify.tracker.di.component

import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.repositories.api.interceptor.AuthInterceptor
import br.net.easify.tracker.background.services.LocationService
import br.net.easify.tracker.di.module.*
import br.net.easify.tracker.repositories.RoutePathRepository
import br.net.easify.tracker.repositories.RouteRepository
import br.net.easify.tracker.repositories.UserRepository
import br.net.easify.tracker.view.adapters.RouteHistoryAdapter
import br.net.easify.tracker.viewmodel.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    DatabaseModule::class,
    PrefsModule::class,
    ServiceModule::class,
    RepositoryModule::class])
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
    fun inject(viewModel: HistoryViewModel)
    fun inject(viewModel: SettingsViewModel)
    fun inject(viewModel: UserDataViewModel)

    fun inject(locationService: LocationService)
    fun inject(interceptor: AuthInterceptor)

    fun inject(repository: UserRepository)
    fun inject(repository: RouteRepository)
    fun inject(repository: RoutePathRepository)

    fun inject(adapter: RouteHistoryAdapter)

}
