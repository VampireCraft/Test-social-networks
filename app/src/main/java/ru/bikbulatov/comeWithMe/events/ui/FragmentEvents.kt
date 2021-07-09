package ru.bikbulatov.comeWithMe.events.ui

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.*
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.domain.DialogAction
import ru.bikbulatov.comeWithMe.core.domain.ViewPagerNavigator
import ru.bikbulatov.comeWithMe.core.geoLocation.GeoLocationListener
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.core.ui.TwoButtonsDialog
import ru.bikbulatov.comeWithMe.databinding.FragmentEventsBinding
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.ui.adapters.NearbyEventsAdapter

class FragmentEvents : BaseFragment(), CardStackListener {
    private lateinit var binding: FragmentEventsBinding
    private lateinit var viewModel: EventsViewModel
    private lateinit var adapter: NearbyEventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(EventsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocationThenGetNearbyEvents()
        observeOnNearbyEvent()
        observeOnDeleteEvent()
        observeSendSpamEvent()
        observeOnAddingUserOnEvent()
        observeOnDeleteUserFromEventResponse()
        observeOnRefuseParticipateResponse()
        refreshOnSwipe()
        configureActionBtns()
    }

    private fun configureActionBtns() {
        binding.ivBtnAccept.setOnClickListener {
            viewModel.acceptEvent(needToUpdate = true)
        }
        binding.ivBtnCancel.setOnClickListener {
            viewModel.refuseEvent(needToUpdate = true)
        }
        binding.ivBtnSpam.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            TwoButtonsDialog(
                positiveBtnText = "Отмена",
                negativeTextBtn = "Пожаловаться",
                title = "Пожаловаться на событие?",
                action = object : DialogAction {
                    override fun onPositiveBtnClicked() {

                    }

                    override fun onNegativeBtnClicked() {
                        viewModel.sendSpam()
                    }

                }
            ).show(parentFragmentManager, TwoButtonsDialog::class.simpleName)

        }
    }

    private fun getLocationThenGetNearbyEvents() {
        (requireActivity() as? GeoLocationListener)?.getLocation { task ->
            var location: Location? = null
            if (task?.result != null)
                location = task.result
            if (location == null) {
                (requireActivity() as? GeoLocationListener)?.getNewLocation()
            } else {
                viewModel.locationLatitude = location.latitude
                viewModel.locationLongitude = location.longitude
                viewModel.getNearbyEvents(location.latitude, location.longitude)
                Log.d("test111", "location latitude ${location.latitude}")
                Log.d("test111", "location longitude ${location.longitude}")
            }
        }
    }

    private fun refreshOnSwipe() {
        binding.swipeRefreshLayout.setDistanceToTriggerSync(300)
        binding.swipeRefreshLayout.setProgressViewEndTarget(false, 0)
        binding.swipeRefreshLayout.setOnRefreshListener {
            getLocationThenGetNearbyEvents()
        }
    }

    private fun observeOnNearbyEvent() {
        viewModel.nearbyEvents.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    Log.d("nearbyEvents", "LOADING")
                    showLoading()
                }

                Status.SUCCESS -> {
                    hideLoading()
                    Log.d("nearbyEvents", "SUCCESS")
                    it.data?.let {
                        if (it.isNotEmpty())
                            configureNearbyEvents(it)
                        else
                            configureEmptyEvents()
                    }
                }

                Status.ERROR -> {
                    Log.d("nearbyEvents", "ERROR")
                    hideLoading()
                    configureEmptyEvents()
                }
            }
            viewModel.sendFcmToken()
        })
    }

    private fun configureNearbyEvents(events: List<EventModel>) {
        binding.ivBtnAccept.visibility = View.VISIBLE
        binding.ivBtnCancel.visibility = View.VISIBLE
        binding.ivBtnRevert.visibility = View.VISIBLE
        binding.ivBtnSpam.visibility = View.VISIBLE
        binding.cardStackView.visibility = View.VISIBLE
        binding.emptyEvents.root.visibility = View.GONE
        initialize(events)
    }

    private fun configureEmptyEvents() {
        binding.ivBtnAccept.visibility = View.GONE
        binding.ivBtnCancel.visibility = View.GONE
        binding.ivBtnRevert.visibility = View.GONE
        binding.ivBtnSpam.visibility = View.GONE
        binding.cardStackView.visibility = View.GONE
        binding.emptyEvents.root.visibility = View.VISIBLE
        binding.emptyEvents.btnCreateEvent.setOnClickListener {
            (requireActivity() as? ViewPagerNavigator)?.openCreateEventScreen()
        }
    }

    private fun observeOnDeleteEvent() {
        viewModel.deleteEventResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("deleteEventResponse", "LOADING")
                Status.SUCCESS -> Log.d("deleteEventResponse", "SUCCESS")
                Status.ERROR -> Log.d("deleteEventResponse", "ERROR")
            }
        })
    }

    private fun observeSendSpamEvent() {
        viewModel.sendSpamResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("deleteEventResponse", "LOADING")
                Status.SUCCESS -> Toast.makeText(requireContext(), "Успешно", Toast.LENGTH_SHORT).show()
                Status.ERROR -> Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeOnAddingUserOnEvent() {
        viewModel.acceptEventResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("addUserOnEventResponse", "LOADING")
                Status.SUCCESS -> {
                    showSuccess(
                        it.data
                            ?: "Запрос на событие отправлен"
                    )
                    Log.d("deleteUserFromEvent", "SUCCESS")
                }
                Status.ERROR -> Log.d("addUserOnEventResponse", "ERROR")
            }
        })
    }

    private fun observeOnDeleteUserFromEventResponse() {
        viewModel.refuseEventResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("deleteUserFromEvent", "LOADING")
                Status.SUCCESS -> {
                    showSuccess(
                        it.data
                            ?: "Успешно проставлен статус \"просмотрено\" для пользователя в событии"
                    )
                    Log.d("deleteUserFromEvent", "SUCCESS")
                }
                Status.ERROR -> Log.d("deleteUserFromEvent", "ERROR")
            }
        })
    }

    private fun observeOnRefuseParticipateResponse() {
        viewModel.refuseParticipateResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("refuseParticipate", "LOADING")
                Status.SUCCESS -> Log.d("refuseParticipate", "SUCCESS")
                Status.ERROR -> Log.d("refuseParticipate", "ERROR")
            }
        })
    }

    override fun showLoading() {
        binding.pbLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.pbLoading.visibility = View.GONE
    }

    override fun showError(error: String) {
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun initialize(events: List<EventModel>) {
        val manager = CardStackLayoutManager(requireContext(), this)
        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(5)
        manager.setTranslationInterval(24.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        binding.cardStackView.layoutManager = manager
        adapter = NearbyEventsAdapter(events, viewModel, parentFragmentManager)
        binding.cardStackView.adapter = adapter
        binding.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        when (direction) {
            Direction.Left -> {
                viewModel.acceptEvent(needToUpdate = true)
            }
            Direction.Right -> {
                viewModel.refuseEvent(needToUpdate = true)
            }
        }
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
        viewModel.eventId = adapter.events[position].id
        Log.d("test123", "onCardAppeared [psition = ${position}")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        if (adapter.events.size - 1 == position)
            Log.d("test123", "cards ended")
    }
}