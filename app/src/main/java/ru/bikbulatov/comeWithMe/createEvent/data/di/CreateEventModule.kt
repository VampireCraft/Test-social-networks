package ru.bikbulatov.comeWithMe.createEvent.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.bikbulatov.comeWithMe.createEvent.data.CreateEventRepositoryImpl
import ru.bikbulatov.comeWithMe.createEvent.domain.CreateEventRepository
import ru.bikbulatov.comeWithMe.createEvent.domain.network.CreateEventApi
import ru.bikbulatov.comeWithMe.events.domain.networkApi.TagsApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CreateEventModule {

    @Singleton
    @Provides
    fun provideCreateEventRepository(
        createEventApi: CreateEventApi,
        tagsApi: TagsApi
    ): CreateEventRepository {
        return CreateEventRepositoryImpl(createEventApi, tagsApi)
    }
}