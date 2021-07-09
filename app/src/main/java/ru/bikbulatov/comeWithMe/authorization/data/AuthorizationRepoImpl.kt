package ru.bikbulatov.comeWithMe.authorization.data

import retrofit2.HttpException
import ru.bikbulatov.comeWithMe.MainPrefs
import ru.bikbulatov.comeWithMe.authorization.domain.AuthorizationRepo
import ru.bikbulatov.comeWithMe.authorization.domain.models.*
import ru.bikbulatov.comeWithMe.authorization.domain.networkApi.LogInApi
import ru.bikbulatov.comeWithMe.authorization.domain.networkApi.SignUpApi
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import javax.inject.Inject

class AuthorizationRepoImpl @Inject constructor(var logInApi: LogInApi, var signUpApi: SignUpApi) :
    AuthorizationRepo {

    override suspend fun authByLogin(
        phone: String,
        password: String
    ): Event<TokenResponse> {
        try {
            val response = logInApi.authByLoginPass(LoginRequest(phone, password))
            response.let {
                MainPrefs.userId = response.data?.userId!!
                TokenRepository.saveTokens(
                    response.data.access ?: "",
                    response.data.refresh ?: ""
                )
                return Event.success(response.data)
            }
        } catch (error: HttpException) {
            error.printStackTrace()
            return Event.error(
                error.response()?.errorBody()?.string()?.substringAfter("error_message\":")
                    ?.filter { it.isLetter() || it.isWhitespace() })
        } catch (e: Exception) {
            e.printStackTrace()
            return Event.error(e.message)
        }
    }

    override suspend fun signUp(
        login: String,
        email: String,
        phoneNumber: String,
        birthDay: String,
        gender: Int,
        photoUrl: String,
        password: String
    ): Event<TokenResponse> {
        try {
            val response = signUpApi.signUp(
                SignUpRequest(
                    name = login,
                    password = password,
                    repeatPassword = password,
                    //email = email,
                    phone = phoneNumber,
                   // birthDay = birthDay,
                    gender = gender,
                   // photoUrl = photoUrl
                )
            )
            response.data?.let {
                return Event.success(response.data)
            }
            return Event.error("Неизвестная ошибка")

        } catch (error: HttpException) {
            error.printStackTrace()
            if (error.response()?.errorBody()?.byteStream().toString()
                    .contains("Отсутсвуют обязательные данные в поле data")
            )
                return Event.error("Отсутсвуют обязательные данные в поле data")
            return Event.error(
                error.response()?.errorBody()?.string()?.substringAfter("error_message\":")
                    ?.filter { it.isLetter() || it.isWhitespace() })
            return Event.error(error.message())
        } catch (e: Exception) {
            e.printStackTrace()
            return Event.error(e.message)
        }
    }

    override suspend fun restorePassword(login: String): Event<String> {
        return try {
            val result =
                logInApi.restorePassword(requestRestorePassword = RequestRestorePassword(login))
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (error: HttpException) {
            Event.error(
                error.response()?.errorBody()?.string()?.substringAfter("error_message\":")
                    ?.filter { it.isLetter() || it.isWhitespace() })
        }
    }

    override suspend fun sendSmsCode(smsCode: String): Event<String> {
        return try {
            val result =
                logInApi.sendSmsCode(requestSmsCode = RequestSmsCode(smsCode.toInt()))
            when (result.statusId) {
                200 -> {
                    Event.success(result.data)
                }
                else -> Event.error(result?.error ?: "Ошибка запроса")
            }
        } catch (error: HttpException) {
            Event.error(
                error.response()?.errorBody()?.string()?.substringAfter("error_message\":")
                    ?.filter { it.isLetter() || it.isWhitespace() })
        }
    }
}