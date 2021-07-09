package ru.bikbulatov.comeWithMe.profile.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.profile.domain.ProfileRepository
import ru.bikbulatov.comeWithMe.profile.domain.models.FeedBackModel
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel

class ProfileViewModel @ViewModelInject constructor(private val profileRepository: ProfileRepository) :
    ViewModel() {

    val userInfo: MutableLiveData<Event<UserModel>> = MutableLiveData()
    fun getUserInfo() {
        userInfo.value = Event.loading()
        viewModelScope.launch {
            userInfo.value = profileRepository.getUserInfo()
        }
    }

    val changePassword: MutableLiveData<Event<Any>> = MutableLiveData()
    fun changePassword(password: String) {
        changePassword.value = Event.loading()
        viewModelScope.launch {
            changePassword.value = profileRepository.changePassword(password)
        }
    }

    val changeUserResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun changeUserInfo(userModel: UserModel) {
        changeUserResponse.value = Event.loading()
        viewModelScope.launch {
            changeUserResponse.value = profileRepository.changeUserInfo(userModel)
            userInfo.value = profileRepository.getUserInfo()
        }
    }

    val acceptedEventsHistory: MutableLiveData<Event<List<EventModel>>> = MutableLiveData()
    fun getAcceptedEventsHistory() {
        acceptedEventsHistory.value = Event.loading()
        viewModelScope.launch {
            acceptedEventsHistory.value = profileRepository.getAcceptedEventsHistory()
        }
    }

    val createdEventsHistory: MutableLiveData<Event<List<EventModel>>> = MutableLiveData()
    fun getCreatedEventsHistory() {
        createdEventsHistory.value = Event.loading()
        viewModelScope.launch {
            createdEventsHistory.value = profileRepository.getCreatedEventsHistory()
        }
    }

    val myFeedBacks: MutableLiveData<Event<List<FeedBackModel>>> = MutableLiveData()
    fun getMyFeedBacks() {
        myFeedBacks.value = Event.loading()
        viewModelScope.launch {
            myFeedBacks.value = profileRepository.getMyFeedBacks()
        }
    }

    val userInfoAndFeedBacks: MutableLiveData<Event<UserModel>> = MutableLiveData()
    fun getUserInfoAndFeedBacks(userId: Int) {
        userInfoAndFeedBacks.value = Event.loading()
        viewModelScope.launch {
            userInfoAndFeedBacks.value = profileRepository.getUserInfoAndFeedBacks(userId)
        }
    }
}