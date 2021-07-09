package ru.bikbulatov.comeWithMe.profile.data

import retrofit2.HttpException
import ru.bikbulatov.comeWithMe.MainPrefs
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import ru.bikbulatov.comeWithMe.profile.domain.FeedbackApi
import ru.bikbulatov.comeWithMe.profile.domain.ProfileRepository
import ru.bikbulatov.comeWithMe.profile.domain.UserApi
import ru.bikbulatov.comeWithMe.profile.domain.models.FcmTokenModel
import ru.bikbulatov.comeWithMe.profile.domain.models.FeedBackModel
import ru.bikbulatov.comeWithMe.profile.domain.models.FeedBackRequest
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel

class ProfileRepositoryImpl(
    var userApi: UserApi,
    private val feedbackApi: FeedbackApi,
    val eventApi: EventApi
) : ProfileRepository {
    override suspend fun getUserInfo(): Event<UserModel> {
        return try {
            val result = userApi.getUser()
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }

    override suspend fun changePassword(password: String): Event<Any> {
        return try {
            val map: MutableMap<String, String?> = mutableMapOf()
            map["password"] = password
            val result = userApi.changePassword(body = map)
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }

    override suspend fun changeUserInfo(userModel: UserModel): Event<String> {
        return try {
            val map: MutableMap<String, String?> = mutableMapOf()
            map["about_me"] = userModel.aboutMe
            map["name"] = userModel.name
            map["birthday"] = userModel.birthday
            map["email"] = userModel.email
            map["photo"] = userModel.photo
            map["gender"] = userModel.gender.toString()
            map["phone"] = userModel.phone
            map["instagram"] = userModel.instagram
            map["telegram"] = userModel.telegram
            map["vk"] = userModel.vk
            map["total_rating"] = userModel.totalRating.toString()
            val result = userApi.changeUserInfo(body = map)
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }

    override suspend fun sendFcmToken(fcmTokenModel: FcmTokenModel): Event<String> {
        return try {
            val map: MutableMap<String, String?> = mutableMapOf()
            map["user"] = fcmTokenModel.user!!.toString()
            map["type"] = "android"
            map["registration_id"] = fcmTokenModel.registrationId.toString()
            val result = userApi.sendFcmToken(body = map)
            when (result.statusId) {
                200 -> {
                    MainPrefs.firebaseTokenIsSend = true
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }

    override suspend fun createFeedback(
        eventId: Int,
        userId: Int,
        appraisal: Int,
        description: String?
    ): Event<String> {
        try {
            val result = feedbackApi.createFeedback(
                body = FeedBackRequest(
                    eventId = eventId,
                    userId = userId,
                    appraisal = appraisal,
                    description = description
                )
            )
            return when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (error: HttpException) {
            return Event.error(
                error.response()?.errorBody()?.string()?.substringAfter("error_message\":")
                    ?.filter { it.isLetter() || it.isWhitespace() })
        } catch (e: Exception) {
            e.printStackTrace()
            return Event.error(e.message)
        }
    }

    override suspend fun getAcceptedEventsHistory(): Event<List<EventModel>> {
        return try {
            val result = eventApi.getAcceptedEventsHistory()
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }

    override suspend fun getCreatedEventsHistory(): Event<List<EventModel>> {
        return try {
            val result = eventApi.getCreatedEventsHistory()
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }

    override suspend fun getMyFeedBacks(): Event<List<FeedBackModel>> {
        return try {
            val result = feedbackApi.getMyFeedbacks()
            when (result.statusId) {
                200 -> {
                    Event.success(result.data?.listFeedbackUser)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }

    override suspend fun getUserInfoAndFeedBacks(userId: Int): Event<UserModel> {
        return try {
            val result = feedbackApi.getUserInfoAndFeedback(userId = userId)
            when (result.statusId) {
                200 -> {
                    Event.success(result.data?.infoUser)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }
}