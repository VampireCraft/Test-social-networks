package ru.bikbulatov.comeWithMe.authorization.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.domain.StepsNavigator
import ru.bikbulatov.comeWithMe.authorization.ui.steps.*
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.databinding.FragmentSignUpCoverBinding

@AndroidEntryPoint
class FragmentSignUp : Fragment(), StepsNavigator {
    companion object {
        const val STEPS_COUNT = 3
    }

    private lateinit var binding: FragmentSignUpCoverBinding
    private lateinit var viewModel: AuthorizationViewModel
    private var currentStep: MutableLiveData<Int> = MutableLiveData()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpCoverBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stepsNavigator = this
        observeOnCurrentStep()
        openFirstStep()
        configureBackBtn()
        binding.tvSignIn.setOnClickListener {
            openSignInFragment()
        }
        currentStep.value = 1
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            openPreviousStep()
        }
    }

    private fun observeOnCurrentStep() {
        currentStep.observe(viewLifecycleOwner, {
            binding.tvStepsCount.text = "$it/$STEPS_COUNT"
            if (currentStep.value == 1)
                binding.ivBtnBack.visibility = View.GONE
            else
                binding.ivBtnBack.visibility = View.VISIBLE
        })
    }

    private fun openPreviousStep() {
        when (currentStep.value) {
            2 -> {
                openFirstStep()
            }
            3 -> {
                openSecondStep()
            }
            4 -> {
                openThirdStep()
            }
            5 -> {
                openThirdStep()
//                openFourthStep()
            }
        }
    }

    override fun openFirstStep() {
        currentStep.value = 1
        childFragmentManager
            .beginTransaction()
            .replace(binding.flStepsRoot.id, FragmentFirstStep())
            .addToBackStack(null)
            .commit()
    }

    override fun openSecondStep() {
        currentStep.value = 2
        childFragmentManager
            .beginTransaction()
            .replace(binding.flStepsRoot.id, FragmentSecondStep())
            .addToBackStack(null)
            .commit()
    }

    override fun openThirdStep() {
        currentStep.value = 2
        childFragmentManager
            .beginTransaction()
            .replace(binding.flStepsRoot.id, FragmentThirdStep())
            .addToBackStack(null)
            .commit()
    }

    override fun openFourthStep() {
        currentStep.value = 3
        childFragmentManager
            .beginTransaction()
            .replace(binding.flStepsRoot.id, FragmentFourthStep())
            .addToBackStack(null)
            .commit()
    }

    override fun openFifthStep() {
        currentStep.value = 3
        childFragmentManager
            .beginTransaction()
            .replace(binding.flStepsRoot.id, FragmentFifthStep())
            .addToBackStack(null)
            .commit()
    }

    private fun openSignInFragment() {
        parentFragmentManager
            .beginTransaction()
            .add(R.id.flAuthRoot, FragmentLogIn())
            .commit()
    }
}