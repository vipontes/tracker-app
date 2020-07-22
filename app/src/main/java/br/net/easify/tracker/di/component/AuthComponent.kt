package br.net.easify.tracker.di.component

import br.net.easify.tracker.di.module.AuthModule
import br.net.easify.tracker.services.LoginService
import br.net.easify.tracker.services.UserService
import dagger.Component

@Component(modules = [AuthModule::class])
interface AuthComponent {
    fun inject(service: LoginService)
}