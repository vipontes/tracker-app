package br.net.easify.tracker.di.module

import android.app.Application
import br.net.easify.tracker.repositories.RoutePathRepository
import br.net.easify.tracker.repositories.RouteRepository
import br.net.easify.tracker.repositories.TokenRepository
import br.net.easify.tracker.repositories.UserRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(application: Application): UserRepository {
        return UserRepository(application)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(application: Application): TokenRepository {
        return TokenRepository(application)
    }

    @Provides
    @Singleton
    fun provideRouteRepository(application: Application): RouteRepository {
        return RouteRepository(application)
    }

    @Provides
    @Singleton
    fun provideRoutePathRepository(application: Application): RoutePathRepository {
        return RoutePathRepository(application)
    }
}