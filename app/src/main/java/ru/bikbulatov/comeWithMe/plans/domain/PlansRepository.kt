package ru.bikbulatov.comeWithMe.plans.domain

import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel

interface PlansRepository {
    suspend fun getAcceptedEvents(): Event<List<EventModel>>
    suspend fun getMyEvents(): Event<List<EventModel>>
}