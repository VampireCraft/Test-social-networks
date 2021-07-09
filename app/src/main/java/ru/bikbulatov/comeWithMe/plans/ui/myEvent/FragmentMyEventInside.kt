package ru.bikbulatov.comeWithMe.plans.ui.myEvent

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.bikbulatov.comeWithMe.core.domain.DialogAction
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.TagView
import ru.bikbulatov.comeWithMe.core.ui.TwoButtonsDialog
import ru.bikbulatov.comeWithMe.databinding.FragmentInsideWhereIGoEventBinding
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel
import ru.bikbulatov.comeWithMe.events.ui.EventsViewModel
import ru.bikbulatov.comeWithMe.plans.ui.FragmentEventMembers
import ru.bikbulatov.comeWithMe.plans.ui.FragmentPlans
import ru.bikbulatov.comeWithMe.plans.ui.PlansViewModel
import java.text.SimpleDateFormat
import java.util.*

class FragmentMyEventInside : Fragment() {
    companion object {
        fun createInstance(event: EventModel): FragmentMyEventInside {
            return FragmentMyEventInside().apply {
                arguments = Bundle().apply {
                    putSerializable("eventModel", event)
                }
            }
        }
    }

    lateinit var event: EventModel
    private lateinit var binding: FragmentInsideWhereIGoEventBinding
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var plansViewModel: PlansViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsideWhereIGoEventBinding.inflate(inflater, container, false)
        eventViewModel = ViewModelProvider(requireActivity()).get(EventsViewModel::class.java)
        plansViewModel = ViewModelProvider(requireActivity()).get(PlansViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEventFromArguments()
        configureBackBtn()
        configureBackgroundImage()
        fillEventInfoFields()
        hideActionBtn()
        configureOrganizerPhone()
        setClickOnMembersBtn()
        configureAdditionalActionBtn()
        if (!event.tags.isNullOrEmpty())
            addTagsToScreen(event.tags)
        observeOnEventDelete()
    }

    private fun addTagsToScreen(tags: List<TagModel>) {
        val referencesIds = IntArray(tags.size)
        for ((i, tag) in tags.withIndex()) {
            if (!binding.tags.referencedIds.contains(tag.id)) {
                val tagView =
                    TagView(requireContext(), tag)
                tagView.id = tag.id
                tagView.ivCancel.visibility = View.GONE
                binding.clRoot.addView(tagView)
                binding.tags.addView(tagView)
                referencesIds[i] = tagView.id
            }
        }
        binding.tags.referencedIds = referencesIds
    }

    private fun configureOrganizerPhone() {
        if (event.eventCreator?.phone?.isNullOrEmpty() == false) {
            binding.btnOrganizerPhoto.text = "Организатор: ${event.eventCreator?.phone}"
            binding.btnOrganizerPhoto.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "8${event.eventCreator?.phone}"))
                startActivity(intent)
            }
        }
    }

    private fun hideActionBtn() {
        binding.llAction.visibility = View.GONE
    }

    private fun configureBackgroundImage() {
        if (event.photoEvent.isNullOrEmpty()) {
            if (event.color != null) {
                drawBackgroundWithGradient(
                    binding.flBackground,
                    event.color?.startColor,
                    event.color?.endColor
                )
                binding.flBackground.visibility = View.VISIBLE
                binding.ivBackground.visibility = View.GONE
            } else
                binding.flBackground.visibility = View.GONE
        } else {
            binding.ivBackground.visibility = View.VISIBLE
            loadPhotoFromUrl(binding.ivBackground, event.photoEvent)
        }
    }

    private fun drawBackgroundWithGradient(view: View, startColor: String?, endColor: String?) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                Color.parseColor(startColor),
                Color.parseColor(endColor)
            )
        )
        gradientDrawable.cornerRadii =
            floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        view.background = gradientDrawable
    }

    private fun getEventFromArguments() {
        event = arguments?.getSerializable("eventModel") as EventModel
    }

    private fun fillEventInfoFields() {
        binding.tvTitle.text = event.name
        binding.tvDescription.text = event.description
        binding.tvPrice.text = event.price.toString()
        binding.tvTime.text = getTimeFromEvent(event.dateEvent?.toLong())
        binding.tvDate.text = getDateFromEvent(event.dateEvent?.toLong())
        binding.tvAddress.text = event.address
    }

    private fun getTimeFromEvent(seconds: Long?): String {
        val date = Date()
        date.time = seconds?.times(1000) ?: 0
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getDateFromEvent(seconds: Long?): String {
        val date = Date()
        date.time = seconds?.times(1000) ?: 0
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun loadPhotoFromUrl(imageView: ImageView, photoUrl: String? = "") {
        Glide
            .with(requireContext())
            .load(photoUrl)
            .centerCrop()
            .into(imageView)
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setClickOnMembersBtn() {
        binding.btnUsers.setOnClickListener {
            binding.root.scrollTo(0, 0)
            parentFragmentManager
                .beginTransaction()
                .replace(
                    binding.flRoot.id,
                    FragmentEventMembers.createInstance(
                        event = event,
                        eventType = FragmentPlans.EVENT_CREATED_BY_ME
                    )
                )
                .addToBackStack(FragmentEventMembers::class.simpleName)
                .commit()
        }
    }

    private fun configureAdditionalActionBtn() {
        binding.btnAdditionalAction.setOnClickListener {
            TwoButtonsDialog(
                positiveBtnText = "Удалить",
                negativeTextBtn = "Оставить",
                title = "Ты уверен, что хочешь удалить событие?",
                action = object : DialogAction {
                    override fun onPositiveBtnClicked() {
                        eventViewModel.deleteEvent(eventId = event.id)
                    }

                    override fun onNegativeBtnClicked() {

                    }
                }
            ).show(parentFragmentManager, TwoButtonsDialog::class.simpleName)
        }
    }

    fun observeOnEventDelete() {
        eventViewModel.deleteEventResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("acceptedEvents", "LOADING")
                Status.SUCCESS -> {
                    parentFragmentManager.popBackStack()
                    plansViewModel.getMyEvents()
                }
                Status.ERROR -> {

                }
            }
        })
    }
}