package ru.bikbulatov.comeWithMe.events.domain.networkApi

import retrofit2.http.*
import ru.bikbulatov.comeWithMe.core.model.ResponseWrapper
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import ru.bikbulatov.comeWithMe.events.domain.AcceptedAndWaitingUsers
import ru.bikbulatov.comeWithMe.events.domain.models.AddUserRequest
import ru.bikbulatov.comeWithMe.events.domain.models.ChangeEventRequest
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.models.EventRequestModel
import ru.bikbulatov.comeWithMe.plans.domain.models.EventMembers

interface EventApi {
    @POST("/api/v0/event/change/")
    suspend fun changeEvent(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body body: ChangeEventRequest
    ): ResponseWrapper<String>

    @GET("/api/v0/event/delete/")
    suspend fun deleteEvent(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Query("event_id") eventId: Int
    ): ResponseWrapper<String>

    @GET("/api/v0/event/list_accepted_events/")
    suspend fun getAcceptedEvents(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken
    ): ResponseWrapper<List<EventModel>>

    @GET("/api/v0/event/list_my_events/")
    suspend fun getMyEvents(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken
    ): ResponseWrapper<List<EventModel>>

    @POST("/api/v0/event/add_user_on_event/")
    suspend fun addUserOnEvent(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body request: AddUserRequest
    ): ResponseWrapper<String>

    //    @DELETE("/api/v0/event/delete_user_from_event/")
    @HTTP(method = "DELETE", path = "/api/v0/event/delete_user_from_event/", hasBody = true)
    suspend fun deleteUserFromEvent(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body request: AddUserRequest
    ): ResponseWrapper<String>

    @POST("/api/v0/event/refuse_participate/")
    suspend fun refuseParticipate(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Query("event_id") eventId: Int,
        @Query("user_id") userId: Int
    ): ResponseWrapper<String>

    @GET("/api/v0/event/events_nearby/")
    suspend fun getNearbyEvents(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Query("coordinate_x") coordinateX: Double,
        @Query("coordinate_y") coordinateY: Double
    ): ResponseWrapper<List<EventModel>>

    @POST("/api/v0/event/accept_event/")
    suspend fun acceptEvent(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body eventId: EventRequestModel
    ): ResponseWrapper<String>

    @POST(" /api/v0/event/refuse_event/")
    suspend fun refuseEvent(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body eventId: EventRequestModel
    ): ResponseWrapper<String>

    @GET("/api/v0/event/get_history_accepted_events/")
    suspend fun getAcceptedEventsHistory(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken
    ): ResponseWrapper<List<EventModel>>

    @GET("/api/v0/event/get_history_created_events/")
    suspend fun getCreatedEventsHistory(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken
    ): ResponseWrapper<List<EventModel>>

    @GET("/api/v0/event/get_list_users_from_events/")
    suspend fun getEventUsers(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Query("event_id") eventId: Int
    ): ResponseWrapper<EventMembers>

    @GET("/api/v0/event/get_user_accept_and_wait_modify/")
    suspend fun getUserAcceptAndWaitModify(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Query("event_id") eventId: Int
    ): ResponseWrapper<AcceptedAndWaitingUsers>

    @GET("/api/v0/event/search_events/coordinate_x=11.1311&coordinate_y=11.1322&tags=1, 2")
    suspend fun searchEventsByTags(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Query("distance") distance: Int,
        @Query("coordinate_x") coordinateX: Double,
        @Query("coordinate_y") coordinateY: Double,
        @Query("tags") tags: List<Int>
    ): ResponseWrapper<List<EventModel>>

    @POST("/api/v0/complaint/event/")
    suspend fun sendSpamEvent(
        @Header("Authorization") jwtToken: String = "JWT " + TokenRepository.accessToken,
        @Body eventId: EventRequestModel
    ): ResponseWrapper<String>
}