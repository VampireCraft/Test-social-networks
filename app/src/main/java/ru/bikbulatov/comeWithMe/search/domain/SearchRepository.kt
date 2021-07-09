package ru.bikbulatov.comeWithMe.search.domain

import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel

interface SearchRepository {
    suspend fun searchEvents(
        distance: Int,
        coordinateX: Double,
        coordinateY: Double,
        tags: List<Int>
    ): Event<List<EventModel>>
}