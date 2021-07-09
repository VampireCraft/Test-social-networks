package ru.bikbulatov.comeWithMe.events.domain.models

import com.google.gson.annotations.SerializedName

class ChangeEventRequest(
    @SerializedName("event_id")
    val eventId: Int,
    val name: String,
    val description: String,
    @SerializedName("coordinate_x")
    val coordinateX: Double,
    @SerializedName("coordinate_y")
    val coordinateY: Double,
    @SerializedName("max_count_users")
    val maxCountUsers: Int,
    val tags: List<TagModel>? = null,
    @SerializedName("search_gender")
    val searchGender: Int? = 0,
    @SerializedName("date_event")
    val dateEvent: Long,
    val color: String? = "",
    val address: String,
    @SerializedName("accept_automatic")
    val acceptAutomatic: Boolean? = false,
    val price: Float? = 0F
)

