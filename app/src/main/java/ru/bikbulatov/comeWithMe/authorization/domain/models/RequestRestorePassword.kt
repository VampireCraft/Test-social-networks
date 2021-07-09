package ru.bikbulatov.comeWithMe.authorization.domain.models


import com.google.gson.annotations.SerializedName

data class RequestRestorePassword(
    @SerializedName("login")
    var login: String? = null
)