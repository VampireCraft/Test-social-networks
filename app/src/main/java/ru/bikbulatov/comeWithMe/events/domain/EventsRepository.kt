package ru.bikbulatov.comeWithMe.events.domain

import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.ChangeEventRequest
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.plans.domain.models.EventMembers

interface EventsRepository {
    suspend fun getNearbyEvents(latitude: Double, longitude: Double): Event<List<EventModel>>
    suspend fun changeEvent(changeEventRequest: ChangeEventRequest): Event<String>
    suspend fun deleteEvent(eventId: Int): Event<String>
    suspend fun getListUsersFromEvents(eventId: Int): Event<EventMembers>
    suspend fun getUserAcceptAndWaitModify(eventId: Int): Event<AcceptedAndWaitingUsers>
    suspend fun acceptEvent(eventId: Int): Event<String>
    suspend fun refuseEvent(eventId: Int): Event<String>
    suspend fun sendSpamEvent(eventId: Int): Event<String>

}