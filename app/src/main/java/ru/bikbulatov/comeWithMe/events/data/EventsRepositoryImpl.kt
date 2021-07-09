package ru.bikbulatov.comeWithMe.events.data

import retrofit2.HttpException
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.AcceptedAndWaitingUsers
import ru.bikbulatov.comeWithMe.events.domain.EventsRepository
import ru.bikbulatov.comeWithMe.events.domain.models.ChangeEventRequest
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.models.EventRequestModel
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import ru.bikbulatov.comeWithMe.plans.domain.models.EventMembers

class EventsRepositoryImpl(val eventApi: EventApi) : EventsRepository {
    override suspend fun getNearbyEvents(
        latitude: Double,
        longitude: Double
    ): Event<List<EventModel>> {
        return try {
            val result = eventApi.getNearbyEvents(coordinateX = latitude, coordinateY = longitude)
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


    override suspend fun changeEvent(changeEventRequest: ChangeEventRequest): Event<String> {
        return try {
            val result = eventApi.changeEvent(body = changeEventRequest)
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

    override suspend fun deleteEvent(eventId: Int): Event<String> {
        return try {
            val result = eventApi.deleteEvent(eventId = eventId)
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

    override suspend fun getListUsersFromEvents(eventId: Int): Event<EventMembers> {
        return try {
            val result = eventApi.getEventUsers(eventId = eventId)
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

    override suspend fun getUserAcceptAndWaitModify(eventId: Int): Event<AcceptedAndWaitingUsers> {
        return try {
            val result = eventApi.getUserAcceptAndWaitModify(eventId = eventId)
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

    override suspend fun acceptEvent(eventId: Int): Event<String> {
        return try {
            val result = eventApi.acceptEvent(eventId = EventRequestModel(eventId))
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

    override suspend fun refuseEvent(eventId: Int): Event<String> {
        return try {
            val result = eventApi.refuseEvent(eventId = EventRequestModel(eventId))
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

    override suspend fun sendSpamEvent(eventId: Int): Event<String> {
        return try {
            val result = eventApi.sendSpamEvent(eventId = EventRequestModel(eventId))
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: HttpException) {
            if (e.code() == 400){
                Event.error("Вы уже отправили жалобу")
            } else {
                Event.error(e.message.toString())
            }
        }
    }
}