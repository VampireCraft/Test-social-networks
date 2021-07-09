package ru.bikbulatov.comeWithMe.authorization.ui.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.core.ui.pickers.DatePickerFragment
import ru.bikbulatov.comeWithMe.core.ui.pickers.PickListener
import ru.bikbulatov.comeWithMe.createEvent.ui.FragmentTimeMoney
import ru.bikbulatov.comeWithMe.databinding.FragmentSignUpSecondStepBinding
import java.util.*

@AndroidEntryPoint
class FragmentSecondStep : BaseFragment() {
    private lateinit var binding: FragmentSignUpSecondStepBinding
    private lateinit var viewModel: AuthorizationViewModel

    private var eventDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpSecondStepBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(requireParentFragment()).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reloadDataFromViewModel()
        configureNextBtn()

        binding.tvBirthDay.setOnClickListener {
            DatePickerFragment(0, object : PickListener {
                override fun onPicked(calendar: Calendar) {
                    eventDate = calendar.timeInMillis
                    viewModel.birthDay = calendar.timeInMillis.toString()
                    setDateFromCalendar(calendar)
                }
            }).show(parentFragmentManager, FragmentTimeMoney::class.simpleName)
        }
    }

    private fun configureNextBtn() {
        binding.btnNext.setOnClickListener {
            if (isDataValidOrShowError())
                viewModel.stepsNavigator.openThirdStep()
        }
    }

    private fun reloadDataFromViewModel() {
        if (!viewModel.birthDay.isNullOrEmpty()) {
            setDateFromCalendar(
                Calendar.getInstance().apply { timeInMillis = viewModel.birthDay.toLong() })
        }
    }

    private fun setDateFromCalendar(calendar: Calendar) {
        binding.tvBirthDay.text =
            "${calendar.get(Calendar.DATE)}.${calendar.get(Calendar.MONTH) + 1}.${
                calendar.get(Calendar.YEAR)
            }"
    }

    private fun isDataValidOrShowError(): Boolean {
        return if (binding.tvBirthDay.text.toString().isNullOrEmpty()) {
            showError("Заполните дату рождения")
            false
        } else true
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(error: String) {
        Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show()
    }
}