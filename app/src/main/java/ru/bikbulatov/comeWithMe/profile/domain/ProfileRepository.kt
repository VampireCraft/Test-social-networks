package ru.bikbulatov.comeWithMe.profile.domain

import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.profile.domain.models.FcmTokenModel
import ru.bikbulatov.comeWithMe.profile.domain.models.FeedBackModel
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel

interface ProfileRepository {
    suspend fun getUserInfo(): Event<UserModel>
    suspend fun changePassword(password: String): Event<Any>
    suspend fun changeUserInfo(userModel: UserModel): Event<String>
    suspend fun sendFcmToken(fcmTokenModel: FcmTokenModel): Event<String>
    suspend fun createFeedback(
        eventId: Int,
        userId: Int,
        appraisal: Int,
        description: String?
    ): Event<String>

    suspend fun getAcceptedEventsHistory(): Event<List<EventModel>>
    suspend fun getCreatedEventsHistory(): Event<List<EventModel>>
    suspend fun getMyFeedBacks(): Event<List<FeedBackModel>>
    suspend fun getUserInfoAndFeedBacks(userId: Int): Event<UserModel>
}