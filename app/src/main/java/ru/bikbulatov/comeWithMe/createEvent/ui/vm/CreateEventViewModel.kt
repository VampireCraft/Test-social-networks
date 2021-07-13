package ru.bikbulatov.comeWithMe.createEvent.ui.vm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.createEvent.domain.CreateEventNavigator
import ru.bikbulatov.comeWithMe.createEvent.domain.CreateEventRepository
import ru.bikbulatov.comeWithMe.createEvent.domain.models.ColorGradient
import ru.bikbulatov.comeWithMe.createEvent.domain.models.CreateEventRequest
import ru.bikbulatov.comeWithMe.createEvent.domain.models.CreateEventRequestPhoto
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel

class CreateEventViewModel @ViewModelInject constructor(val createEventRepository: CreateEventRepository) :
    ViewModel() {
    var navigator: CreateEventNavigator? = null

    var eventCreationRequest = CreateEventRequest()
    //var eventCreationRequestPhoto = CreateEventRequestPhoto()
    val createEventResponse: MutableLiveData<Event<String>> = MutableLiveData()
    fun createEvent() {
        createEventResponse.value = Event.loading()
        viewModelScope.launch {
            createEventResponse.value = createEventRepository.createEvent(eventCreationRequest)
        }
    }

//    private fun prepareStringPart(item:String): MultipartBody.Part{
//        return MultipartBody.Part.createFormData()
//    }

    val colorGradients: MutableLiveData<Event<List<ColorGradient>>> = MutableLiveData()
    fun getColorGradients() {
        colorGradients.value = Event.loading()
        viewModelScope.launch {
            colorGradients.value = createEventRepository.getColorGradients()
        }
    }

    val tags: MutableLiveData<Event<List<TagModel>>> = MutableLiveData()
    val selectedTags: MutableLiveData<MutableList<TagModel>> =
        MutableLiveData<MutableList<TagModel>>(mutableListOf())

    fun getTags() {
        tags.value = Event.loading()
        viewModelScope.launch {
            tags.value = createEventRepository.getTags()
        }
    }

    val sendPhoto: MutableLiveData<Event<String>> = MutableLiveData()
    fun sendPhoto(photoPath: String) {
        sendPhoto.value = Event.loading()
        viewModelScope.launch {
            sendPhoto.value = createEventRepository.sendPhoto(photoPath)
        }
    }
}