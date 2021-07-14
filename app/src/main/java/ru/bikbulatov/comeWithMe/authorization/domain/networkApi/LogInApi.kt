package ru.bikbulatov.comeWithMe.authorization.domain.networkApi

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import ru.bikbulatov.comeWithMe.authorization.domain.models.*
import ru.bikbulatov.comeWithMe.core.model.ResponseWrapper
import ru.bikbulatov.comeWithMe.core.network.TokenRepository

interface LogInApi {
    @POST("/api/v0/auth/token-auth/")
    suspend fun authByLoginPass(@Body body: LoginRequest): ResponseWrapper<TokenResponse>

    @POST("/api/v0/auth/token-refresh/")
    fun refreshToken(@Body body: RefreshTokenRequest): Observable<ResponseWrapper<RefreshTokenResponse>>

    @POST("/api/v0/user/password/send_code/")
    suspend fun restorePassword(
        //@Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body requestRestorePassword: RequestRestorePassword
    ): ResponseWrapper<String>

    @POST("/api/v0/user/password/check_code/")
    suspend fun sendSmsCode(
        //@Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body requestSmsCode: RequestSmsCode
    ): ResponseWrapper<String>

    @POST("/api/v0/user/password/change_password/")
    suspend fun sendNewPass(
        @Body requestNewPassword: RequestNewPassword
    ): ResponseWrapper<String>

    @POST("/api/v0/register/check_phone_and_send_code/")
    suspend fun checkPhone(
        //@Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body requestCheckPhone: RequestCheckPhone
    ): ResponseWrapper<String>

    @POST("/api/v0/register/check_code/")
    suspend fun sendSmsCodeLogIn(
        //@Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body requestSmsCode: RequestSmsCode
    ): ResponseWrapper<String>
}
