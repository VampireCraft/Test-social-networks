package ru.bikbulatov.comeWithMe.profile.domain

import retrofit2.http.*
import ru.bikbulatov.comeWithMe.core.model.ResponseWrapper
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel

interface UserApi {
    @GET("/api/v0/user/")
    suspend fun getUser(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken
    ): ResponseWrapper<UserModel>

    //todo change model
    @PATCH("/api/v0/user/change/")
    suspend fun changeUserInfo(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body body: MutableMap<String, String?>
    ): ResponseWrapper<String>

    @POST("/api/v0/user/fcm-device/")
    suspend fun sendFcmToken(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body body: MutableMap<String, String?>
    ): ResponseWrapper<String>

    @POST("/api/v0/user/password/change/")
    suspend fun changePassword(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body body: MutableMap<String, String?>
    ): ResponseWrapper<String>
}