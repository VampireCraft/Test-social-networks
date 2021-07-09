package ru.bikbulatov.comeWithMe.events.data

import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.EventsManager
import ru.bikbulatov.comeWithMe.events.domain.models.AddUserRequest
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi

class EventsManagerImpl(val eventApi: EventApi) : EventsManager {
    override suspend fun addUserOnEvent(eventId: Int, userId: Int): Event<String> {
        return try {
            val result = eventApi.addUserOnEvent(
                request = AddUserRequest(
                    eventId = eventId,
                    userId = userId
                )
            )
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

    override suspend fun deleteUserFromEvent(eventId: Int, userId: Int): Event<String> {
        return try {
            val result = eventApi.deleteUserFromEvent(
                request = AddUserRequest(
                    eventId = eventId,
                    userId = userId
                )
            )
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

    override suspend fun refuseParticipate(eventId: Int, userId: Int): Event<String> {
        return try {
            val result = eventApi.refuseParticipate(eventId = eventId, userId = userId)
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
}