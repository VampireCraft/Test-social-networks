package ru.bikbulatov.comeWithMe.authorization.domain.models


import com.google.gson.annotations.SerializedName

data class RequestRestorePassword(
    @SerializedName("phone")
    var login: Long? = null
)