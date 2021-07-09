package ru.bikbulatov.comeWithMe.profile.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import ru.bikbulatov.comeWithMe.profile.domain.FeedbackApi
import ru.bikbulatov.comeWithMe.profile.domain.ProfileRepository
import ru.bikbulatov.comeWithMe.profile.domain.UserApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Singleton
    @Provides
    fun provideProfileRepository(
        userApi: UserApi,
        feedbackApi: FeedbackApi,
        eventApi: EventApi
    ): ProfileRepository {
        return ProfileRepositoryImpl(userApi, feedbackApi, eventApi)
    }
}