package ru.bikbulatov.comeWithMe.events.domain

import ru.bikbulatov.comeWithMe.core.model.Event

interface EventsManager {
    suspend fun addUserOnEvent(eventId: Int, userId: Int): Event<String>
    suspend fun deleteUserFromEvent(eventId: Int, userId: Int): Event<String>
    suspend fun refuseParticipate(eventId: Int, userId: Int): Event<String>
}