package ru.bikbulatov.comeWithMe.createEvent.domain.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ColorGradient(
    val id: Int,
    @SerializedName("start_color")
    val startColor: String,
    @SerializedName("end_color")
    val endColor: String,
    @SerializedName("color_text")
    val textColor: String,
    val angle: Int,
) : Serializable
