package ru.bikbulatov.comeWithMe.createEvent.domain.models

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel
import java.io.File

class CreateEventRequest(
    //var multipart: MultipartBody.Part,
    var name: String,
    var description: String,
    @SerializedName("coordinate_x")
    var coordinateX: Double,
    @SerializedName("coordinate_y")
    var coordinateY: Double,
    @SerializedName("max_count_users")
    var maxCountUsers: Int,
   // @SerializedName("category")
    var tags: List<TagModel>? = null,
    @SerializedName("search_gender")
    var searchGender: Int? = 0,
    @SerializedName("date_event")
    var dateEvent: Long,
    @SerializedName("color_id")
    var colorId: Int? = null,
    var address: String,
    @SerializedName("url_site")
    var urlSite: String,
    @SerializedName("photo_event")
    var photo: String?,
    @SerializedName("accept_automatic")
    var acceptAutomatic: Boolean? = false,
    @SerializedName("is_online")
    var isOnline: Boolean? = false,
    var price: Float? = 0F
) {
    constructor() : this(
        //MultipartBody.Part.createFormData("n",""),
        "",
        "",
        0.0,
        0.0,
        0,
        emptyList(),
        0,
        0,
        1,
        "",
        "",
        null,
        false
    )

//    fun createMultipart(){
//        var multipartLocal = MultipartBody.Part.createFormData("name",name)
//
//    }
}

class CreateEventRequestPhoto(
    @SerializedName("photo_event")
    var photo: MultipartBody.Part
) {
    constructor():this(
        MultipartBody.Part.createFormData("n","")
    )
}

