package ru.bikbulatov.comeWithMe.authorization.domain.models


import com.google.gson.annotations.SerializedName

data class RequestSmsCode(
    @SerializedName("code")
    var code: Int? = null
)