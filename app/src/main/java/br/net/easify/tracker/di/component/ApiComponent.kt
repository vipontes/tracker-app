package br.net.easify.tracker.di.component

import br.net.easify.tracker.di.module.ApiModule
import br.net.easify.tracker.di.module.LoginModule
import br.net.easify.tracker.di.module.UserModule
import br.net.easify.tracker.services.LoginService
import br.net.easify.tracker.services.UserService
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ApiModule::class, LoginModule::class, UserModule::class])
@Singleton
interface ApiComponent {
    fun inject(service: LoginService)
    fun inject(service: UserService)

}