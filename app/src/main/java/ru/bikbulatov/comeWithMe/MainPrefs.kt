package ru.bikbulatov.comeWithMe

import com.chibatching.kotpref.KotprefModel

object MainPrefs : KotprefModel() {

    var firebaseToken by stringPref()
    var firebaseTokenIsSend by booleanPref(false)
    var userId by intPref()

}