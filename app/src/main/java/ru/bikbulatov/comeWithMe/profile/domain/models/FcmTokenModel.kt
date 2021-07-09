package ru.bikbulatov.comeWithMe.profile.domain.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FcmTokenModel(
    @SerializedName("registration_id")
    var registrationId: String? = null,
    var type: String? = null,
    var user: Int? = null
): Serializable