package ru.bikbulatov.comeWithMe.plans.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import ru.bikbulatov.comeWithMe.plans.data.PlansRepositoryImpl
import ru.bikbulatov.comeWithMe.plans.domain.PlansRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlansModule {

    @Singleton
    @Provides
    fun provideAuthRepo(eventApi: EventApi): PlansRepository {
        return PlansRepositoryImpl(eventApi)
    }
}