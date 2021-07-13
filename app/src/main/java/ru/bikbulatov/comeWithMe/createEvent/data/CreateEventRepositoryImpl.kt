package ru.bikbulatov.comeWithMe.createEvent.data

import android.graphics.Bitmap
import android.util.Log
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.createEvent.domain.CreateEventRepository
import ru.bikbulatov.comeWithMe.createEvent.domain.models.ColorGradient
import ru.bikbulatov.comeWithMe.createEvent.domain.models.CreateEventRequest
import ru.bikbulatov.comeWithMe.createEvent.domain.models.CreateEventRequestPhoto
import ru.bikbulatov.comeWithMe.createEvent.domain.network.CreateEventApi
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel
import ru.bikbulatov.comeWithMe.events.domain.networkApi.TagsApi
import java.io.File


class CreateEventRepositoryImpl(val createEventApi: CreateEventApi, val tagsApi: TagsApi) :
    CreateEventRepository {

    override suspend fun createEvent(createEventRequest: CreateEventRequest): Event<String> {
        try {
            val result = createEventApi.createEvent(body = createEventRequest)
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

    override suspend fun getTags(): Event<List<TagModel>> {
        try {
            val result = tagsApi.getTags()
            when (result.statusId) {
                200 -> {
                    Log.d("get Tags", result.data.toString())
                    return Event.success(result.data)
                }
                else -> return Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            return Event.error(e.message.toString())
        }
    }

    override suspend fun getColorGradients(): Event<List<ColorGradient>> {
        try {
            val result = createEventApi.getColorGradients()
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

    override suspend fun sendPhoto(imageUri: String): Event<String> {
        return try {
            val result = createEventApi.sendPhoto(file = prepareFilePart(imageUri))
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result.error ?: "Ошибка запроса")
            }
        } catch (e: Exception) {
            Event.error(e.message.toString())
        }
    }

      private fun prepareFilePart(imageUri: String): MultipartBody.Part {
        val file = File(imageUri)
        val requestFile = RequestBody.create(
            MediaType.parse("image/*"),
            file
        )
        return MultipartBody.Part.createFormData("photo_event", file.name, requestFile)
    }

}