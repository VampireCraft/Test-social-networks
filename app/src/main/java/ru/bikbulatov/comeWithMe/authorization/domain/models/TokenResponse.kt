package ru.bikbulatov.comeWithMe.authorization.domain.models

import com.google.gson.annotations.SerializedName

class TokenResponse(
    val access: String,
    val refresh: String,
    @SerializedName("user_id")
    val userId: Int
)
