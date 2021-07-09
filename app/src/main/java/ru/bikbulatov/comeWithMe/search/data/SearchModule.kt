package ru.bikbulatov.comeWithMe.search.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import ru.bikbulatov.comeWithMe.search.domain.SearchRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {
    @Singleton
    @Provides
    fun provideSearchRepository(eventApi: EventApi): SearchRepository {
        return SearchRepositoryImpl(eventApi)
    }
}