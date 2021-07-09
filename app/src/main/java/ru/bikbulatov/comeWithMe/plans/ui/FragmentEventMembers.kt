package ru.bikbulatov.comeWithMe.plans.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_inside_event.view.*
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.databinding.FragmentMembersBinding
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.ui.EventsViewModel
import ru.bikbulatov.comeWithMe.plans.ui.adapter.MembersAdapter
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel
import ru.bikbulatov.comeWithMe.profile.ui.FragmentAboutUser

@AndroidEntryPoint
class FragmentEventMembers : Fragment() {
    companion object {
        const val ACCEPTED_USER_LIST = 0
        const val MODERATING_USER_LIST = 1

        fun createInstance(event: EventModel, eventType: Int): FragmentEventMembers {
            return FragmentEventMembers().apply {
                arguments = Bundle().apply {
                    putSerializable("event", event)
                    putInt("eventType", eventType)
                }
            }
        }
    }

    private lateinit var binding: FragmentMembersBinding
    private lateinit var viewModel: EventsViewModel

    lateinit var event: EventModel
    var eventType: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMembersBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EventsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEventIdFromArguments()
        getEventTypeFromArguments()
        configureBackBtn()
        loadMembers()
        observeOnEventMembers()
        observeOnUserAcceptAndWaitModify()
        observeOnAddUserOnEvent()
        observeOnDeleteUserOnEvent()
        observeOnDeleteCounter()
        configureDeleteTimerDialog()
        initClickers()
    }

    private fun initClickers() {
        binding.itemOrganizer.clUserRoot.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.clMembersRoot,
                    FragmentAboutUser.createInstance(userId = event.eventCreator?.id!!)
                )
                .addToBackStack(FragmentAboutUser::class.simpleName)
                .commit()
        }
    }

    private fun loadMembers() {
        if (eventType == FragmentPlans.EVENT_WHERE_I_GO)
            viewModel.getEventMembers(event.id)
        else
            viewModel.getUserAcceptAndWaitModify(event.id)
    }

    private fun configureDeleteTimerDialog() {
        binding.timerDialog.root.setOnClickListener {
            viewModel.timer?.cancel()
            binding.timerDialog.root.visibility = View.GONE
        }
    }

    private fun observeOnDeleteCounter() {
        viewModel.timerCounter.observe(viewLifecycleOwner, {
            if (it > 0) {
                binding.timerDialog.root.visibility = View.VISIBLE
                binding.timerDialog.tvTimerCount.text = it.toString()
            } else {
                binding.timerDialog.root.visibility = View.GONE
            }
        })
    }

    private fun observeOnAddUserOnEvent() {
        viewModel.addUserOnEventResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("addUserOnEventResponse", "LOADING")
                Status.SUCCESS -> {
                    it.data?.let { message ->
                        showSuccessMessage(message)
                    }
                    loadMembers()
                }
                Status.ERROR -> Log.d("addUserOnEventResponse", "ERROR")
            }
        })
    }

    private fun observeOnDeleteUserOnEvent() {
        viewModel.deleteUserFromEventResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    it.data?.let { message ->
                        showSuccessMessage(message)
                    }
                    loadMembers()
                }
                Status.ERROR -> Log.d("deleteUserFromEvent", "ERROR")
            }
        })
    }

    private fun observeOnEventMembers() {
        viewModel.eventMembersResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("eventMembersResponse", "LOADING")
                Status.SUCCESS -> {
                    Log.d("eventMembersResponse", "SUCCESS")
                    it.data?.let {
                        configureAcceptedMembers(it.users)
                        configureOrganizer(it.organizer)
                    }
                }
                Status.ERROR -> Log.d("eventMembersResponse", "ERROR")
            }
        })
    }

    private fun observeOnUserAcceptAndWaitModify() {
        viewModel.userAcceptAndWaitModifyResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("UserAcceptAndWaitModify", "LOADING")
                Status.SUCCESS -> {
                    Log.d("UserAcceptAndWaitModify", "SUCCESS")
                    it.data?.let { acceptedAndWaitingUsers ->
                        configureOrganizer(acceptedAndWaitingUsers.organizer)
                        configureAcceptedMembers(acceptedAndWaitingUsers.acceptedUsers)
                        if (!acceptedAndWaitingUsers.listWaitModify.isNullOrEmpty())
                            configureWaitingUsers(acceptedAndWaitingUsers.listWaitModify)
                    }
                }
                Status.ERROR -> Log.d("UserAcceptAndWaitModify", "ERROR")
            }
        })
    }

    private fun getEventIdFromArguments() {
        event = requireArguments().getSerializable("event") as EventModel
    }

    private fun getEventTypeFromArguments() {
        eventType = requireArguments().getInt("eventType")
    }

    private fun configureAcceptedMembers(users: List<UserModel>) {
        binding.tvAcceptedMembersTitle.text =
            "Принятые участники (${users.size}/${event.maxCountUsers})"
        binding.rvAcceptedEventMembers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAcceptedEventMembers.adapter =
            MembersAdapter(
                users,
                parentFragmentManager,
                eventType,
                ACCEPTED_USER_LIST,
                eventId = event.id,
                viewModel
            )
    }

    private fun configureWaitingUsers(waitingUsers: List<UserModel>) {
        binding.tvWaitingMembersTitle.visibility = View.VISIBLE
        binding.rvWaitingEventMembers.visibility = View.VISIBLE
        binding.rvWaitingEventMembers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvWaitingEventMembers.adapter =
            MembersAdapter(
                users = waitingUsers,
                fragmentManager = parentFragmentManager,
                eventType = eventType,
                membersListType = MODERATING_USER_LIST,
                eventId = event.id,
                viewModel = viewModel
            )
    }

    private fun configureOrganizer(organizer: UserModel?) {
        binding.itemOrganizer.tvUserName.text = organizer?.name
        binding.itemOrganizer.tvRating.text = organizer?.totalRating.toString()
        loadPhotoFromUrl(binding.itemOrganizer.ivUserPhoto, organizer?.photo)
    }


    private fun loadPhotoFromUrl(imageView: ImageView, photoUrl: String? = "") {
        Glide
            .with(imageView.context)
            .load(photoUrl)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.ic_default_profile_photo)
            .into(imageView)
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}