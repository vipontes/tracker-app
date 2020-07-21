package br.net.easify.tracker.di.component

import br.net.easify.tracker.di.module.ApiModule
import br.net.easify.tracker.services.LoginService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: LoginService)
}