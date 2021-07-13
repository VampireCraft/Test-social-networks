package ru.bikbulatov.comeWithMe.plans.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.plans.domain.PlansRepository
import ru.bikbulatov.comeWithMe.plans.ui.myEvent.FragmentMyEvents
import ru.bikbulatov.comeWithMe.plans.ui.myEvent.MyEventManager

class PlansViewModel @ViewModelInject constructor(private val plansRepository: PlansRepository) :
    ViewModel() {


    val acceptedEvents: MutableLiveData<Event<List<EventModel>>> = MutableLiveData()
    fun getAcceptedEvents() {
        acceptedEvents.value = Event.loading()
        viewModelScope.launch {
            acceptedEvents.value = plansRepository.getAcceptedEvents()
        }
    }

    val myEvents: MutableLiveData<Event<List<EventModel>>> = MutableLiveData()
    fun getMyEvents() {
        myEvents.value = Event.loading()
        viewModelScope.launch {
            myEvents.value = plansRepository.getMyEvents()
        }
    }
}