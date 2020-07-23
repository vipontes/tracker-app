package br.net.easify.tracker.di.module

import br.net.easify.tracker.services.UserService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserModule {

    @Provides
    @Singleton
    fun provideUserService(): UserService {
        return UserService()
    }
}