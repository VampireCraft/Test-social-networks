package ru.bikbulatov.comeWithMe.createEvent.ui

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.domain.models.Gender
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.createEvent.ui.vm.CreateEventViewModel
import ru.bikbulatov.comeWithMe.databinding.FragmentCreateSettingsBinding

class FragmentCreateEventSettings : BaseFragment() {
    private lateinit var binding: FragmentCreateSettingsBinding
    private lateinit var viewModel: CreateEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(CreateEventViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBackBtn()
        configureBtnNext()
        configureRadioButtonsGroups()
        paintProgressBar()
    }

    private fun configureBtnNext() {
        binding.btnNext.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            viewModel.eventCreationRequest.acceptAutomatic = binding.rbAccessAll.isChecked
            viewModel.eventCreationRequest.searchGender =
                when {
                    binding.rbOnlyBoys.isChecked -> Gender.BOY.id
                    binding.rbOnlyGirls.isChecked -> Gender.GIRL.id
                    else -> 0
                }
            viewModel.navigator?.openTimeMoneyScreen()
        }
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun configureRadioButtonsGroups() {
        binding.rbAutoModerate.setOnCheckedChangeListener { it, isChecked ->
            if (isChecked) {
                binding.rbManualPick.isChecked = false
                binding.rbManualPick.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bg_help
                    )
                )
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_additional))
            }
        }
        binding.rbManualPick.setOnCheckedChangeListener { it, isChecked ->
            if (isChecked) {
                binding.rbAutoModerate.isChecked = false
                binding.rbAutoModerate.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bg_help
                    )
                )
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_additional))
            }
        }


        binding.rbAccessAll.setOnCheckedChangeListener { it, isChecked ->
            if (isChecked) {
                binding.rbOnlyGirls.apply {
                    this.isChecked = false
                    this.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_help))
                }
                binding.rbOnlyBoys.apply {
                    this.isChecked = false
                    this.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_help))
                }
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_additional))
            }
        }
        binding.rbOnlyGirls.setOnCheckedChangeListener { it, isChecked ->
            if (isChecked) {
                binding.rbAccessAll.apply {
                    this.isChecked = false
                    this.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_help))
                }
                binding.rbOnlyBoys.apply {
                    this.isChecked = false
                    this.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_help))
                }
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_additional))
            }
        }
        binding.rbOnlyBoys.setOnCheckedChangeListener { it, isChecked ->
            if (isChecked) {
                binding.rbOnlyGirls.apply {
                    this.isChecked = false
                    this.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_help))
                }
                binding.rbAccessAll.apply {
                    this.isChecked = false
                    this.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_help))
                }
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_additional))
            }
        }
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
            binding.pbEventCreate.setProgress(60, true)
        } else
            binding.pbEventCreate.progress = 60
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }

    override fun showError(error: String) {
        TODO("Not yet implemented")
    }
}