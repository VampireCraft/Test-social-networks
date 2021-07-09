package ru.bikbulatov.comeWithMe.plans.domain.models

import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel

class EventMembers(
    val organizer: UserModel,
    val users: List<UserModel>
)