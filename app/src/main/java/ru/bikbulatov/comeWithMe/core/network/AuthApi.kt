package ru.bikbulatov.comeWithMe.core.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import ru.bikbulatov.comeWithMe.core.domain.TokenRequest
import ru.bikbulatov.comeWithMe.core.domain.TokenResponse

interface AuthApi {
    @POST("api/v0/auth/token-refresh/")
    fun refreshTokens(@Body refreshToken: TokenRequest): Call<TokenResponse>

}