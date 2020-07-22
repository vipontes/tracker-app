package br.net.easify.tracker.di.component

import br.net.easify.tracker.di.module.ApiModule
import br.net.easify.tracker.services.LoginService
import br.net.easify.tracker.services.UserService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: LoginService)
    fun inject(service: UserService)
}