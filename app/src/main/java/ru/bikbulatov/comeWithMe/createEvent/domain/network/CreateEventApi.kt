package ru.bikbulatov.comeWithMe.createEvent.domain.network

import okhttp3.MultipartBody
import retrofit2.http.*
import ru.bikbulatov.comeWithMe.core.model.ResponseWrapper
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import ru.bikbulatov.comeWithMe.createEvent.domain.models.ColorGradient
import ru.bikbulatov.comeWithMe.createEvent.domain.models.CreateEventRequest

interface CreateEventApi {


    @POST("/api/v0/event/create/")
    suspend fun createEvent(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body body: CreateEventRequest
    ): ResponseWrapper<String>

    @GET(" /api/v0/color_gradients/")
    suspend fun getColorGradients(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken
    ): ResponseWrapper<List<ColorGradient>>

    @Multipart
    @POST(" /api/v0/event/send_photo/")
    suspend fun sendPhoto(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Part file: MultipartBody.Part
    ): ResponseWrapper<String>
}