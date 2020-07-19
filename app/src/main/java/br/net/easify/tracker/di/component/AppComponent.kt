package br.net.easify.tracker.di.component


import br.net.easify.tracker.MainApplication
import br.net.easify.tracker.di.module.AppModule
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent?
        fun application(appModule: AppModule): Builder
    }

    fun inject(app: MainApplication)
}