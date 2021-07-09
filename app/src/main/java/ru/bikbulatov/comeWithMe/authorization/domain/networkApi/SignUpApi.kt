package ru.bikbulatov.comeWithMe.authorization.domain.networkApi

import retrofit2.http.Body
import retrofit2.http.POST
import ru.bikbulatov.comeWithMe.authorization.domain.models.SignUpRequest
import ru.bikbulatov.comeWithMe.authorization.domain.models.TokenResponse
import ru.bikbulatov.comeWithMe.core.model.ResponseWrapper

interface SignUpApi {
    @POST("api/v0/register/registration/")
    suspend fun signUp(@Body body: SignUpRequest): ResponseWrapper<TokenResponse>
}