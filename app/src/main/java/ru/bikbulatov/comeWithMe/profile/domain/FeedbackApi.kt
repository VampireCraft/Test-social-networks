package ru.bikbulatov.comeWithMe.profile.domain

import retrofit2.http.*
import ru.bikbulatov.comeWithMe.core.model.ResponseWrapper
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import ru.bikbulatov.comeWithMe.profile.domain.models.FeedBackRequest
import ru.bikbulatov.comeWithMe.profile.domain.models.UserInfoResponse

interface FeedbackApi {
    @POST("/api/v0/feedback/create/")
    suspend fun createFeedback(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body body: FeedBackRequest
    ): ResponseWrapper<String>

    @GET("/api/v0/feedback/info_user_feedback/")
    suspend fun getUserInfoAndFeedback(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Query("user_id") userId: Int
    ): ResponseWrapper<UserInfoResponse>

    @GET("/api/v0/feedback/my_feedback/")
    suspend fun getMyFeedbacks(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken
    ): ResponseWrapper<MyFeedBacksResponse>
}