package ru.bikbulatov.comeWithMe.profile.domain.models

import com.google.gson.annotations.SerializedName

class UserInfoResponse(
    @SerializedName("info_user")
    val infoUser: UserModel
)