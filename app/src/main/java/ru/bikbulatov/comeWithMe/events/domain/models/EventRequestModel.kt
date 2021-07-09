package ru.bikbulatov.comeWithMe.events.domain.models

import com.google.gson.annotations.SerializedName

class EventRequestModel(
    @SerializedName("event_id")
    var eventId: Int
)