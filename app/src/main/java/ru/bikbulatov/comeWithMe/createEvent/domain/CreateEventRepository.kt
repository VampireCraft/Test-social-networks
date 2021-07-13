package ru.bikbulatov.comeWithMe.createEvent.domain

import okhttp3.MultipartBody
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.createEvent.domain.models.ColorGradient
import ru.bikbulatov.comeWithMe.createEvent.domain.models.CreateEventRequest
import ru.bikbulatov.comeWithMe.createEvent.domain.models.CreateEventRequestPhoto
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel

interface CreateEventRepository {
    suspend fun createEvent(createEventRequest: CreateEventRequest): Event<String>
    suspend fun getTags(): Event<List<TagModel>>
    suspend fun sendPhoto(imageUri: String): Event<String>
    //fun prepareFilePart(imageUri: String): MultipartBody.Part
    suspend fun getColorGradients(): Event<List<ColorGradient>>
}