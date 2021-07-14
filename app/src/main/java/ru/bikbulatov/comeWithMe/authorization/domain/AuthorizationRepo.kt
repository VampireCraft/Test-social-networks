package ru.bikbulatov.comeWithMe.authorization.domain

import ru.bikbulatov.comeWithMe.authorization.domain.models.TokenResponse
import ru.bikbulatov.comeWithMe.core.model.Event

interface AuthorizationRepo {
    suspend fun authByLogin(
        phone: String,
        password: String,
    ): Event<TokenResponse>

    suspend fun signUp(
        login: String,
        email: String,
        phoneNumber: String,
        birthDay: String,
        gender: Int,
        photoUrl: String,
        password: String
    ): Event<TokenResponse>

    suspend fun restorePassword(login: String): Event<String>

    suspend fun sendSmsCode(smsCode: String): Event<String>

    suspend fun sendNewPass(code:String,pass:String):Event<String>

    suspend fun checkPhone(phone: String): Event<String>

    suspend fun sendSmsCodeLogIn(smsCode: String): Event<String>
}