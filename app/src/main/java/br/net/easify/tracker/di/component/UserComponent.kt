package br.net.easify.tracker.di.component

import br.net.easify.tracker.di.module.UserModule
import br.net.easify.tracker.services.UserService
import dagger.Component

@Component(modules = [UserModule::class])
interface UserComponent {
    fun inject(service: UserService)
}