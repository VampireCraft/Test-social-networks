package ru.bikbulatov.comeWithMe.events.domain

import com.google.gson.annotations.SerializedName
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel

class AcceptedAndWaitingUsers(
    val organizer: UserModel,
    @SerializedName("list_wait_modify")
    val listWaitModify: List<UserModel>,
    @SerializedName("accepted_users")
    val acceptedUsers: List<UserModel>
)
