package ru.bikbulatov.comeWithMe.plans.data

import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import ru.bikbulatov.comeWithMe.plans.domain.PlansRepository

class PlansRepositoryImpl(val eventApi: EventApi) : PlansRepository {
    override suspend fun getAcceptedEvents(): Event<List<EventModel>> {
        try {
            val result = eventApi.getAcceptedEvents()
            when (result.statusId) {
                200 -> {
                    return Event.success(result.data)
                }
                else -> return Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            return Event.error(e.message.toString())
        }
    }

    override suspend fun getMyEvents(): Event<List<EventModel>> {
        try {
            val result = eventApi.getMyEvents()
            when (result.statusId) {
                200 -> {
                    return Event.success(result.data)
                }
                else -> return Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            return Event.error(e.message.toString())
        }
    }
}