package ru.bikbulatov.comeWithMe.events.domain.models

import com.google.gson.annotations.SerializedName
import ru.bikbulatov.comeWithMe.createEvent.domain.models.ColorGradient
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel
import java.io.Serializable

class EventModel(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val tags: List<TagModel>,
    val color: ColorGradient?,
    val price: Float,
    @SerializedName("max_count_users")
    val maxCountUsers: Int,
    @SerializedName("is_online")
    val isOnline: Boolean,
    @SerializedName("create_event")
    val eventCreator: UserModel?,
    @SerializedName("photo_event")
    val photoEvent: String,
    @SerializedName("date_event")
    val dateEvent: String?
) : Serializable

