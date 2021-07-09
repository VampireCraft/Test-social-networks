package ru.bikbulatov.comeWithMe.events.domain.models

import com.google.gson.annotations.SerializedName

class AddUserRequest(
    @SerializedName("event_id")
    val eventId: Int,
    @SerializedName("user_id")
    val userId: Int
)