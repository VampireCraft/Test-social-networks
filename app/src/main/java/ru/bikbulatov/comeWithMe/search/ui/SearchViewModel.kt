package ru.bikbulatov.comeWithMe.search.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.search.domain.SearchRepository

class SearchViewModel @ViewModelInject constructor(val searchRepository: SearchRepository) :
    ViewModel() {

    val foundEvents: MutableLiveData<Event<List<EventModel>>> = MutableLiveData()
    fun searchEvents(distance: Int, latitude: Double, longitude: Double, tags: List<Int>) {
        foundEvents.value = Event.loading()
        viewModelScope.launch {
            foundEvents.value = searchRepository.searchEvents(distance, latitude, longitude, tags)
        }
    }
}