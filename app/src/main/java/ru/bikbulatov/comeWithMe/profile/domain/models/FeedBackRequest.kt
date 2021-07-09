package ru.bikbulatov.comeWithMe.profile.domain.models

import com.google.gson.annotations.SerializedName

class FeedBackRequest(
    @SerializedName("event_id")
    val eventId: Int,
    val appraisal: Int,
    val description: String?,
    @SerializedName("user_id")
    val userId: Int
)