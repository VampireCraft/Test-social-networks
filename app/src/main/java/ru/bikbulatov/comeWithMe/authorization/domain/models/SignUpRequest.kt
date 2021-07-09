package ru.bikbulatov.comeWithMe.authorization.domain.models

import com.google.gson.annotations.SerializedName

class SignUpRequest(
    val name: String,
    val password: String,
    @SerializedName("repeat_password")
    val repeatPassword: String,
    //val email: String,
    val phone: String,
    //val birthDay: String,
    val gender: Int,
    //val photoUrl: String
)
