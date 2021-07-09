package ru.bikbulatov.comeWithMe.events.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.databinding.DialogGetFeedbackBinding
import ru.bikbulatov.comeWithMe.events.data.Utils
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel

class RatingDialog : DialogFragment() {

    companion object {
        fun createInstance(event: EventModel): RatingDialog {
            return RatingDialog().apply {
                arguments = Bundle().apply {
                    putSerializable("eventModel", event)
                }
            }
        }
    }

    private lateinit var binding: DialogGetFeedbackBinding
    private lateinit var viewModel: EventsViewModel
    private var filledStarDrawable: Drawable? = null
    private var emptyStarDrawable: Drawable? = null

    lateinit var event: EventModel
    private var starsCount = MutableLiveData(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogGetFeedbackBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(EventsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEventFromArguments()
        fillFields()
        configureStarsLogic()
        observeOnStarsCount()
        configureCancelBtn()
        configureFeedBackBtn()
        observeOnFeedBackResponse()
        filledStarDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_filled_star)
        emptyStarDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_star)
        for (i in 0 until 4) {
            fillStar(starsList.value[i])
        }
    }

    private fun configureCancelBtn() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun configureFeedBackBtn() {
        binding.btnFeedBack.setOnClickListener {
            viewModel.createFeedback(
                eventId = event.id,
                userId = event.eventCreator?.id!!,
                appraisal = starsCount.value!!,
                description = binding.etComment.text.toString()
            )
        }
    }

    private fun getEventFromArguments() {
        event = arguments?.getSerializable("eventModel") as EventModel
    }

    private fun fillFields() {
        binding.tvEventName.text = event.name
        binding.tvOrganizerName.text = event.eventCreator?.name + ", " + Utils.calcOrganizerAge(
            event.eventCreator?.birthday?.toLong()
        )

        loadPhotoFromUrl(binding.ivOrganizerPhoto, event.eventCreator?.photo)
    }

    private fun loadPhotoFromUrl(imageView: ImageView, photoUrl: String? = "") {
        Glide
            .with(requireContext())
            .load(photoUrl)
            .centerCrop()
            .into(imageView)
    }

    private val starsList = lazy {
        listOf<ImageView>(
            binding.ivOneStar,
            binding.ivTwoStars,
            binding.ivThreeStar,
            binding.ivFourStar,
            binding.ivFiveStar
        )
    }

    private fun configureStarsLogic() {
        binding.ivOneStar.setOnClickListener {
            starsCount.value = 1
        }
        binding.ivTwoStars.setOnClickListener {
            starsCount.value = 2
        }
        binding.ivThreeStar.setOnClickListener {
            starsCount.value = 3
        }
        binding.ivFourStar.setOnClickListener {
            starsCount.value = 4
        }
        binding.ivFiveStar.setOnClickListener {
            starsCount.value = 5
        }
    }

    private fun observeOnStarsCount() {
        starsCount.observe(viewLifecycleOwner, {
            for (i in it until starsList.value.size) {
                clearStar(starsList.value[i])
            }
            for (i in 0 until it) {
                fillStar(starsList.value[i])
            }
        })
    }

    private fun fillStar(imageView: ImageView) {
        if (imageView.drawable != filledStarDrawable)
            imageView.setImageDrawable(filledStarDrawable)
    }

    private fun clearStar(imageView: ImageView) {
        if (imageView.drawable != emptyStarDrawable)
            imageView.setImageDrawable(emptyStarDrawable)
    }

    private fun observeOnFeedBackResponse() {
        viewModel.feedbackResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("feedbackResponse", "LOADING")
                Status.SUCCESS -> {
                    showMessage(it.data.toString())
                    dismiss()
                    Log.d("feedbackResponse", "SUCCESS")
                }
                Status.ERROR -> {
                    showMessage(it.error.toString())
                    Log.d("feedbackResponse", "ERROR")
                }
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}