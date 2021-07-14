package ru.bikbulatov.comeWithMe.authorization.domain.models

import com.google.gson.annotations.SerializedName

data class RequestNewPassword(
    var code: Int? = null,
    @SerializedName("new_password")
    var newPassword: String? = null
)
