package ru.bikbulatov.comeWithMe.authorization.ui.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.bikbulatov.comeWithMe.authorization.domain.AuthorizationRepo
import ru.bikbulatov.comeWithMe.authorization.domain.StepsNavigator
import ru.bikbulatov.comeWithMe.authorization.domain.models.TokenResponse
import ru.bikbulatov.comeWithMe.core.model.Event

class AuthorizationViewModel @ViewModelInject constructor(private val authorizationRepo: AuthorizationRepo) :
    ViewModel() {
    lateinit var stepsNavigator: StepsNavigator
    var userName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var birthDay: String = ""
    var gender: Int = 0
    var photoUrl: String = ""
    var password: String = ""
    var code: String = ""

    var logInResponse: MutableLiveData<Event<TokenResponse>> = MutableLiveData()
    fun logIn(login: String, password: String) {
        logInResponse.value = Event.loading()
        CoroutineScope(Dispatchers.IO).launch {
            logInResponse.postValue(authorizationRepo.authByLogin(login, password))
        }
    }

    var signUpResponse: MutableLiveData<Event<TokenResponse>> = MutableLiveData()
    fun signUp(_password: String) {
        signUpResponse.value = Event.loading()
        CoroutineScope(Dispatchers.IO).launch {
            signUpResponse.postValue(
                authorizationRepo.signUp(
                    login = userName,
                    email = email,
                    phoneNumber = phoneNumber,
                    birthDay = birthDay,
                    gender = gender,
                    photoUrl = photoUrl,
                    password = _password
                )
            )
        }
    }

    var restorePasswordResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun restorePassword(login: String){
        restorePasswordResponse.value = Event.loading()
        CoroutineScope(Dispatchers.IO).launch {
            restorePasswordResponse.postValue(authorizationRepo.restorePassword(login))
        }
    }

    var smsCodeResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun sendSmsCode(smsCode: String) {
        smsCodeResponse.value = Event.loading()
        CoroutineScope(Dispatchers.IO).launch {
            smsCodeResponse.postValue(authorizationRepo.sendSmsCode(smsCode))
        }
    }

    var newPassResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun sendNewPass(code: String, pass: String) {
        newPassResponse.value = Event.loading()
        CoroutineScope(Dispatchers.IO).launch {
            newPassResponse.postValue(authorizationRepo.sendNewPass(code, pass))
        }
    }
}