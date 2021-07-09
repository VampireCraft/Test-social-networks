package ru.bikbulatov.comeWithMe.events.data

import java.util.*

object Utils {
    fun calcOrganizerAge(birthDay: Long?): String {
        if (birthDay == null)
            return " "
        val calendar = Calendar.getInstance().apply {
            timeInMillis = birthDay * 1000
        }
        return (Calendar.getInstance().get(Calendar.YEAR) - calendar.get(Calendar.YEAR)).toString()
    }
}