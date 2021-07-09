package ru.bikbulatov.comeWithMe.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.bikbulatov.comeWithMe.authorization.domain.networkApi.LogInApi
import ru.bikbulatov.comeWithMe.authorization.domain.networkApi.SignUpApi
import ru.bikbulatov.comeWithMe.createEvent.domain.network.CreateEventApi
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import ru.bikbulatov.comeWithMe.events.domain.networkApi.TagsApi
import ru.bikbulatov.comeWithMe.profile.domain.FeedbackApi
import ru.bikbulatov.comeWithMe.profile.domain.UserApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideLoginApi(): LogInApi {
        return NetworkHolder.getLogInApi()
    }

    @Singleton
    @Provides
    fun provideSignUpApi(): SignUpApi {
        return NetworkHolder.getSignUpApi()
    }

    @Singleton
    @Provides
    fun provideEventApi(): EventApi {
        return NetworkHolder.getEventApi()
    }

    @Singleton
    @Provides
    fun provideUserApi(): UserApi {
        return NetworkHolder.getUserApi()
    }

    @Singleton
    @Provides
    fun provideTagsApi(): TagsApi {
        return NetworkHolder.getTagsApi()
    }

    @Singleton
    @Provides
    fun provideFeedbackApi(): FeedbackApi {
        return NetworkHolder.getFeedbackApi()
    }

    @Singleton
    @Provides
    fun provideCreateEventApi(): CreateEventApi {
        return NetworkHolder.getCreateEventApi()
    }
}