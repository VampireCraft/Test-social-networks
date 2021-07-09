package ru.bikbulatov.comeWithMe.events.domain.networkApi

import retrofit2.http.GET
import retrofit2.http.Header
import ru.bikbulatov.comeWithMe.core.model.ResponseWrapper
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel

interface TagsApi {
    @GET("/api/v0/tags/list_tags")
    suspend fun getTags(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken
    ): ResponseWrapper<List<TagModel>>
}