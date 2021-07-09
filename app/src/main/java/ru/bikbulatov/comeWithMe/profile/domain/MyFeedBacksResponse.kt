package ru.bikbulatov.comeWithMe.profile.domain

import com.google.gson.annotations.SerializedName
import ru.bikbulatov.comeWithMe.profile.domain.models.FeedBackModel

class MyFeedBacksResponse(
    @SerializedName("list_feedback_user")
    val listFeedbackUser: List<FeedBackModel>
)