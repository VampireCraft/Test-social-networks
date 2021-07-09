package ru.bikbulatov.comeWithMe.search.data

import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.networkApi.EventApi
import ru.bikbulatov.comeWithMe.search.domain.SearchRepository

class SearchRepositoryImpl(val eventApi: EventApi) : SearchRepository {
    override suspend fun searchEvents(
        distance: Int,
        coordinateX: Double,
        coordinateY: Double,
        tags: List<Int>
    ): Event<List<EventModel>> {
        return try {
            val result = eventApi.searchEventsByTags(
                distance = distance,
                coordinateX = coordinateX,
                coordinateY = coordinateY,
                tags = tags
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
}