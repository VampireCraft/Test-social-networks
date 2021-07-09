package ru.bikbulatov.comeWithMe.authorization.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.bikbulatov.comeWithMe.authorization.data.AuthorizationRepoImpl
import ru.bikbulatov.comeWithMe.authorization.domain.AuthorizationRepo
import ru.bikbulatov.comeWithMe.authorization.domain.networkApi.LogInApi
import ru.bikbulatov.comeWithMe.authorization.domain.networkApi.SignUpApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthRepo(logInApi: LogInApi, signUpApi: SignUpApi): AuthorizationRepo {
        return AuthorizationRepoImpl(logInApi, signUpApi)
    }
}