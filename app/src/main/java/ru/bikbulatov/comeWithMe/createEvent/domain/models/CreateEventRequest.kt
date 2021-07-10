package ru.bikbulatov.comeWithMe.createEvent.domain.models

import android.util.Base64
import com.google.gson.annotations.SerializedName
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel

class CreateEventRequest(
    var name: String,
    var description: String,
    @SerializedName("coordinate_x")
    var coordinateX: Double,
    @SerializedName("coordinate_y")
    var coordinateY: Double,
    @SerializedName("max_count_users")
    var maxCountUsers: Int,
    var tags: List<TagModel>? = null,
    @SerializedName("search_gender")
    var searchGender: Int? = 0,
    @SerializedName("date_event")
    var dateEvent: Long,
    @SerializedName("color_id")
    var colorId: Int? = null,
    var address: String,
    @SerializedName("photo_event")
    var photo: String,
    @SerializedName("accept_automatic")
    var acceptAutomatic: Boolean? = false,
    var price: Float? = 0F
) {
    constructor() : this(
        "",
        "",
        0.0,
        0.0,
        0,
        emptyList(),
        0,
        0,
        1,
        "",
        "",
        false
    )
}

