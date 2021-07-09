package ru.bikbulatov.comeWithMe.events.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.bikbulatov.comeWithMe.events.data.EventsManagerImpl
import ru.bikbulatov.comeWithMe.events.data.EventsRepositoryImpl
import ru.bikbulatov.comeWithMe.events.domain.EventsManager
import ru.bikbulatov.comeWithMe.events.domain.EventsRepository
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EventsModule {

    @Singleton
    @Provides
    fun provideEventsRepository(eventsApi: EventApi): EventsRepository {
        return EventsRepositoryImpl(eventsApi)
    }

    @Singleton
    @Provides
    fun provideEventsManager(eventsApi: EventApi): EventsManager {
        return EventsManagerImpl(eventsApi)
    }
}