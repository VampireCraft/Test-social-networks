package ru.bikbulatov.comeWithMe.createEvent.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_choose_color.view.*
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.createEvent.domain.models.ColorGradient
import ru.bikbulatov.comeWithMe.createEvent.ui.adapters.ColorsAdapter
import ru.bikbulatov.comeWithMe.createEvent.ui.vm.CreateEventViewModel
import ru.bikbulatov.comeWithMe.databinding.FragmentChooseColorBinding
import java.io.File

private const val ARG_TYPE_CREATE_EVENT = "ARG_TYPE_CREATE_EVENT"
private const val ARG_TYPE_PHOTO_PATH = "ARG_TYPE_PHOTO_PATH"

class FragmentChooseColor : BaseFragment() {
    private lateinit var binding: FragmentChooseColorBinding
    private lateinit var viewModel: CreateEventViewModel
    private lateinit var typeCreateEvent: CreateEventTypeEnum
    private lateinit var photoPath: String

    fun newInstance(typeCreateEvent: CreateEventTypeEnum, absolutePath: String): FragmentChooseColor {
        val fragment = FragmentChooseColor()
        val args = Bundle()
        args.putSerializable(ARG_TYPE_CREATE_EVENT, typeCreateEvent)
        args.putString(ARG_TYPE_PHOTO_PATH, absolutePath)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        typeCreateEvent = arguments?.getSerializable(ARG_TYPE_CREATE_EVENT) as CreateEventTypeEnum
        photoPath = arguments?.getString(ARG_TYPE_PHOTO_PATH).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseColorBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(CreateEventViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBtnNext()
        configureBackBtn()
        paintProgressBar()

        when (typeCreateEvent){
            CreateEventTypeEnum.EVENT_COLOR->{
                viewModel.getColorGradients()
                observeOnColorGradients()
            }
            CreateEventTypeEnum.EVENT_CAMERA->{
                viewModel.sendPhoto(photoPath)
                binding.tvTitleToolbar.text = "Фото"
                Glide.with(requireView())
                    .load(File(photoPath))
                    .into(binding.ivPhoto)
                binding.ivPhoto.visibility = View.VISIBLE
                binding.llPickedColor.visibility = View.GONE
            }
            CreateEventTypeEnum.EVENT_GALLERY->{
                viewModel.sendPhoto(photoPath)
                binding.tvTitleToolbar.text = "Фото"
                Glide.with(requireView())
                    .load(File(photoPath))
                    .into(binding.ivPhoto)
                binding.ivPhoto.visibility = View.VISIBLE
                binding.llPickedColor.visibility = View.GONE
            }
        }

        viewModel.sendPhoto.observe(viewLifecycleOwner, {
            viewModel.eventCreationRequest.photo = it.data.toString()
        })
    }

    private fun paintProgressBar() {
        binding.pbEventCreate.indeterminateDrawable.colorFilter =
            PorterDuffColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.bright_turquoise
                ), PorterDuff.Mode.SRC_IN
            )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.pbEventCreate.setProgress(20, true)
        } else
            binding.pbEventCreate.progress = 20
    }

    private fun configureBtnNext() {
        binding.btnNext.setOnClickListener {
            binding.btnNext.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.btn_click))
            viewModel.navigator?.openDescriptionScreen()
        }
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeOnColorGradients() {
        viewModel.colorGradients.observe(viewLifecycleOwner, { colors ->
            when (colors.status) {
                Status.LOADING -> {
                    showLoading()
                }
                Status.SUCCESS -> {
                    hideLoading()
                    colors.data?.let {
                        configureColorsView(it)
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                }
            }
        })
    }

    private fun configureColorsView(colors: List<ColorGradient>) {
        binding.rvColors.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvColors.adapter = ColorsAdapter(colors, object : ColorPickListener {
            override fun onColorPicked(color: ColorGradient) {
                viewModel.eventCreationRequest.colorId = color.id
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    intArrayOf(
                        Color.parseColor(color.startColor),
                        Color.parseColor(color.endColor)
                    )
                )
                gradientDrawable.cornerRadius = 90f
                binding.llPickedColor.background = gradientDrawable
            }
        })
    }

    override fun showLoading() {
        binding.llPickedColor.apply {
            startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.loading))
        }
    }

    override fun hideLoading() {
        binding.llPickedColor.clearAnimation()
    }

    override fun showError(error: String) {
        //todo
    }
}