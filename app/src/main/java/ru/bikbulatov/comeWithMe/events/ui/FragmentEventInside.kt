package ru.bikbulatov.comeWithMe.events.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.domain.DialogAction
import ru.bikbulatov.comeWithMe.core.ui.TagView
import ru.bikbulatov.comeWithMe.core.ui.TwoButtonsDialog
import ru.bikbulatov.comeWithMe.databinding.FragmentInsideEventBinding
import ru.bikbulatov.comeWithMe.events.data.Utils
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel
import ru.bikbulatov.comeWithMe.profile.ui.FragmentAboutUser
import java.text.SimpleDateFormat
import java.util.*

class FragmentEventInside : Fragment() {
    companion object {
        fun createInstance(event: EventModel): FragmentEventInside {
            return FragmentEventInside().apply {
                arguments = Bundle().apply {
                    putSerializable("eventModel", event)
                }
            }
        }
    }

    lateinit var event: EventModel
    private lateinit var binding: FragmentInsideEventBinding
    private lateinit var viewModel: EventsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsideEventBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(EventsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEventFromArguments()
        configureBackBtn()
        configureBackgroundImage()
        fillEventInfoFields()
        fillOrganizerFields()
        configureActionBtns()
        configureRatingDialog()
        if (!event.tags.isNullOrEmpty())
            addTagsToScreen(event.tags)
    }

    private fun configureRatingDialog() {
        val clickListener = View.OnClickListener {
            RatingDialog.createInstance(event)
                .show(childFragmentManager, RatingDialog::class.simpleName)
        }
        binding.ivRating.setOnClickListener(clickListener)
        binding.tvRating.setOnClickListener(clickListener)
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

    private fun fillOrganizerFields() {
        binding.tvOrganizerName.text = event.eventCreator?.name + ", " + Utils.calcOrganizerAge(
            event.eventCreator?.birthday?.toLong()
        )
        binding.tvRating.text = event.eventCreator?.totalRating.toString()
        loadPhotoFromUrl(binding.ivOrganizerPhoto, event?.eventCreator?.photo)
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

        binding.clOrganizer.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.clRoot,
                    FragmentAboutUser.createInstance(userId = event.eventCreator?.id!!)
                )
                .addToBackStack(FragmentAboutUser::class.simpleName)
                .commit()
        }
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

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun configureActionBtns() {
        binding.ivBtnAccept.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            viewModel.acceptEvent(_eventId = event.id, needToUpdate = true)
            activity?.onBackPressed()
        }

        binding.ivBtnCancel.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            viewModel.refuseEvent(_eventId = event.id, needToUpdate = true)
            activity?.onBackPressed()
        }

        binding.tvComplaintEvent.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            TwoButtonsDialog(
                positiveBtnText = "Отмена",
                negativeTextBtn = "Пожаловаться",
                title = "Пожаловаться на событие?",
                action = object : DialogAction {
                    override fun onPositiveBtnClicked() {

                    }

                    override fun onNegativeBtnClicked() {
                        viewModel.sendSpamForId(eventId = event.id)
                    }

                }
            ).show(parentFragmentManager, TwoButtonsDialog::class.simpleName)
        }
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
}