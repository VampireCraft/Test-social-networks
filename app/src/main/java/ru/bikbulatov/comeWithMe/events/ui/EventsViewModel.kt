package ru.bikbulatov.comeWithMe.events.ui

import android.os.CountDownTimer
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.bikbulatov.comeWithMe.MainPrefs
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.AcceptedAndWaitingUsers
import ru.bikbulatov.comeWithMe.events.domain.EventsManager
import ru.bikbulatov.comeWithMe.events.domain.EventsRepository
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.plans.domain.PlansRepository
import ru.bikbulatov.comeWithMe.plans.domain.models.EventMembers
import ru.bikbulatov.comeWithMe.plans.ui.myEvent.MyEventManager
import ru.bikbulatov.comeWithMe.profile.domain.ProfileRepository
import ru.bikbulatov.comeWithMe.profile.domain.models.FcmTokenModel
import kotlin.math.round


class EventsViewModel @ViewModelInject constructor(
    private val eventsRepository: EventsRepository,
    private val profileRepository: ProfileRepository,
    private val plansRepository: PlansRepository,
    private val eventsManager: EventsManager,
    //private val myEventManager: MyEventManager
) :
    ViewModel() {
    var locationLatitude: Double = 0.0
    var locationLongitude: Double = 0.0


    val nearbyEvents: MutableLiveData<Event<List<EventModel>>> = MutableLiveData()
    fun getNearbyEvents(
        latitude: Double = locationLatitude,
        longitude: Double = locationLongitude
    ) {
        nearbyEvents.value = Event.loading()
        viewModelScope.launch {
            nearbyEvents.postValue(eventsRepository.getNearbyEvents(latitude, longitude))
        }
    }

    var eventId = 0
    val acceptEventResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun acceptEvent(_eventId: Int = eventId, needToUpdate: Boolean) {
        acceptEventResponse.value = Event.loading()
        viewModelScope.launch {
            acceptEventResponse.postValue(eventsRepository.acceptEvent(_eventId))
            if (needToUpdate) {
                nearbyEvents.value = Event.loading()
                nearbyEvents.postValue(
                    eventsRepository.getNearbyEvents(
                        locationLatitude,
                        locationLongitude
                    )
                )
            }
            plansRepository.getAcceptedEvents()
        }
    }



    fun sendFcmToken() {
        viewModelScope.launch {
            profileRepository.sendFcmToken(
                FcmTokenModel(
                    MainPrefs.firebaseToken,
                    "android",
                    MainPrefs.userId
                )
            )
        }
    }

    val refuseEventResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun refuseEvent(_eventId: Int = eventId, needToUpdate: Boolean) {
        refuseEventResponse.value = Event.loading()

        viewModelScope.launch {
            Log.d("test123", "refuseEvent_eventId= ${_eventId}")
            refuseEventResponse.value = eventsRepository.refuseEvent(_eventId)
            if (needToUpdate) {
                nearbyEvents.value = Event.loading()
                nearbyEvents.postValue(
                    eventsRepository.getNearbyEvents(
                        locationLatitude,
                        locationLongitude
                    )
                )
            }
            plansRepository.getAcceptedEvents()
        }
    }

    val eventMembersResponse: MutableLiveData<Event<EventMembers>> = MutableLiveData()
    fun getEventMembers(eventId: Int) {
        refuseEventResponse.value = Event.loading()
        viewModelScope.launch {
            eventMembersResponse.postValue(eventsRepository.getListUsersFromEvents(eventId = eventId))
        }
    }

    val userAcceptAndWaitModifyResponse: MutableLiveData<Event<AcceptedAndWaitingUsers>> =
        MutableLiveData()

    fun getUserAcceptAndWaitModify(eventId: Int) {
        refuseEventResponse.value = Event.loading()
        viewModelScope.launch {
            userAcceptAndWaitModifyResponse.postValue(
                eventsRepository.getUserAcceptAndWaitModify(
                    eventId = eventId
                )
            )
        }
    }

    val feedbackResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun createFeedback(eventId: Int, userId: Int, appraisal: Int, description: String?) {
        feedbackResponse.value = Event.loading()
        viewModelScope.launch {
            feedbackResponse.postValue(
                profileRepository.createFeedback(
                    eventId = eventId,
                    userId = userId,
                    appraisal = appraisal,
                    description = description
                )
            )
        }
    }

    val deleteEventResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun deleteEvent(eventId: Int) {
        deleteEventResponse.value = Event.loading()
        viewModelScope.launch {
            deleteEventResponse.postValue(eventsRepository.deleteEvent(eventId))
        }
    }

    val sendSpamResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun sendSpam() {
        viewModelScope.launch {
            sendSpamResponse.postValue(eventsRepository.sendSpamEvent(eventId))
        }
    }


    fun sendSpamForId(eventId: Int) {
        viewModelScope.launch {
            sendSpamResponse.postValue(eventsRepository.sendSpamEvent(eventId))
        }
    }

    val addUserOnEventResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun addUserOnEvent(eventId: Int, userId: Int) {
        addUserOnEventResponse.value = Event.loading()
        viewModelScope.launch {
            addUserOnEventResponse.postValue(eventsManager.addUserOnEvent(eventId, userId))
        }
    }

    val deleteUserFromEventResponse: MutableLiveData<Event<String>> = MutableLiveData()
    var timerCounter = MutableLiveData(0)
    var timer: CountDownTimer? = null
    fun deleteUserFromEvent(eventId: Int, userId: Int) {
        timerCounter.value = 5
        timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerCounter.value = (round(millisUntilFinished / 1000F)).toInt()
            }

            override fun onFinish() {
                timerCounter.value = 0
                deleteUserFromEventResponse.value = Event.loading()
                viewModelScope.launch {
                    deleteUserFromEventResponse.value =
                        eventsManager.deleteUserFromEvent(eventId, userId)
                }
            }
        }
        timer?.start()

    }


    val refuseParticipateResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun refuseParticipate(eventId: Int, userId: Int) {
        refuseParticipateResponse.value = Event.loading()
        viewModelScope.launch {
            refuseParticipateResponse.value = eventsManager.deleteUserFromEvent(eventId, userId)
        }
    }

}