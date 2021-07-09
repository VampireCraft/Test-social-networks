package ru.bikbulatov.comeWithMe.profile.domain.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserModel(
    val id: Int,
    var name: String?,
    var birthday: String?,
    var photo: String?,
    var email: String?,
    @SerializedName("about_me")
    var aboutMe: String?,
    var gender: Int?,
    var phone: String?,
    var telegram: String?,
    var instagram: String?,
    var vk: String?,
    @SerializedName("total_rating")
    var totalRating: Float
) : Serializable
