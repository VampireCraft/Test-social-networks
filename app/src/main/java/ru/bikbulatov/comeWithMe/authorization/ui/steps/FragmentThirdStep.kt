package ru.bikbulatov.comeWithMe.authorization.ui.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.domain.models.Gender
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.databinding.FragmentSignUpThirdStepBinding

@AndroidEntryPoint
class FragmentThirdStep : BaseFragment() {
    private lateinit var binding: FragmentSignUpThirdStepBinding
    private lateinit var viewModel: AuthorizationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpThirdStepBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(requireParentFragment()).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNextBtn()
        configureRadioBtns()
    }

    private fun configureRadioBtns() {
        binding.rbBoy.setOnCheckedChangeListener { it, isChecked ->
            //if (it.isPressed) {
            if (isChecked) {
                binding.rbGirl.isChecked = !isChecked
                binding.rbGirl.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bg_help
                    )
                )
                binding.rbOther.isChecked = !isChecked
                binding.rbOther.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bg_help
                    )
                )
                viewModel.gender = Gender.BOY.id
                it.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bright_turquoise
                    )
                )
            }
            //}
        }
        binding.rbGirl.setOnCheckedChangeListener { it, isChecked ->
            //if (it.isPressed) {
            if (isChecked) {
                binding.rbBoy.isChecked = !isChecked
                binding.rbBoy.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bg_help
                    )
                )
                binding.rbOther.isChecked = !isChecked
                binding.rbOther.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bg_help
                    )
                )
                viewModel.gender = Gender.GIRL.id
                it.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bright_turquoise
                    )
                )
            }
            //}
        }
        binding.rbOther.setOnCheckedChangeListener { it, isChecked ->
            //if (it.isPressed) {
            if (isChecked) {
                binding.rbBoy.isChecked = !isChecked
                binding.rbBoy.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bg_help
                    )
                )
                binding.rbGirl.isChecked = !isChecked
                binding.rbGirl.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bg_help
                    )
                )
                viewModel.gender = Gender.OTHER.id
                it.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.bright_turquoise
                    )
                )
            }
            //}
        }
//        binding.rbBoy.setOnCheckedChangeListener { _, isChecked ->
//            binding.rbGirl.isChecked = !isChecked
//            binding.rbOther.isChecked = !isChecked
//        }
//
//        binding.rbGirl.setOnCheckedChangeListener { _, isChecked ->
//            binding.rbBoy.isChecked = !isChecked
//            binding.rbOther.isChecked = !isChecked
//        }
//
//        binding.rbOther.setOnCheckedChangeListener { _, isChecked ->
//            binding.rbBoy.isChecked = !isChecked
//            binding.rbGirl.isChecked = !isChecked
//        }
    }

    private fun configureNextBtn() {
        binding.btnNext.setOnClickListener {
//            if (binding.rbBoy.isChecked) {
//                viewModel.gender = Gender.BOY.id
//            } else {
//                if (binding.rbGirl.isChecked) {
//                    viewModel.gender = Gender.GIRL.id
//                } else {
//                    viewModel.gender = Gender.OTHER.id
//                }
//            }
            viewModel.stepsNavigator.openFifthStep()
        }
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