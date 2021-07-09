package ru.bikbulatov.comeWithMe.createEvent.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.core.ui.pickers.DatePickerFragment
import ru.bikbulatov.comeWithMe.core.ui.pickers.PickListener
import ru.bikbulatov.comeWithMe.core.ui.pickers.TimePickerFragment
import ru.bikbulatov.comeWithMe.createEvent.ui.vm.CreateEventViewModel
import ru.bikbulatov.comeWithMe.databinding.FragmentCreateTimeMoneyBinding
import java.util.*

class FragmentTimeMoney : BaseFragment() {
    private lateinit var binding: FragmentCreateTimeMoneyBinding
    private lateinit var viewModel: CreateEventViewModel

    private var eventDate: Long = 0
    private var eventTime: Long = 0
    private var eventCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateTimeMoneyBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(CreateEventViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBtnNext()
        configureBackBtn()
        paintProgressBar()
        observeOnTextFields()
    }

    private fun observeOnTextFields() {
        binding.tvDate.setOnClickListener {
            DatePickerFragment(Calendar.getInstance().timeInMillis, object : PickListener {
                override fun onPicked(calendar: Calendar) {
                    setDateFromCalendar(calendar)
                }
            }).show(parentFragmentManager, FragmentTimeMoney::class.simpleName)
        }
        binding.tvTime.setOnClickListener {
            TimePickerFragment(object : PickListener {
                override fun onPicked(calendar: Calendar) {
                    setTimeFromCalendar(calendar)
                }
            }).show(parentFragmentManager, FragmentTimeMoney::class.simpleName)
        }

        binding.tilPrice.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    viewModel.eventCreationRequest.price = s.toString()?.toFloat()
                } catch (e: Exception) {
                    showError("Неверный формат данных")
                }
            }
        })
    }

    private fun setDateFromCalendar(calendar: Calendar) {
        eventDate = calendar.timeInMillis
        eventCalendar.set(Calendar.DATE, calendar.get(Calendar.DATE))
        eventCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        eventCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        binding.tvDate.text =
            "${calendar.get(Calendar.DATE)}.${calendar.get(Calendar.MONTH) + 1}.${
                calendar.get(Calendar.YEAR)
            }"
    }

    private fun setTimeFromCalendar(calendar: Calendar) {
        eventTime = calendar.timeInMillis
        eventCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        eventCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        binding.tvTime.text =
            "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE) + 1}"
    }

    private fun configureBtnNext() {
        binding.btnNext.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            when {
                eventTime <= 0 -> showError("Выберите время мероприятия")
                eventDate <= 0 -> showError("Выберите дату мероприятия")
                viewModel.eventCreationRequest.price!! < 0 -> showError("Введите стоимость мероприятия")
                else -> {
                    viewModel.eventCreationRequest.dateEvent = eventCalendar.timeInMillis / 1000
                    viewModel.navigator?.openPositionScreen()
                }
            }
        }
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun paintProgressBar() {
        binding.pbEventCreate.progressDrawable.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.bright_turquoise
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.pbEventCreate.setProgress(80, true)
        } else
            binding.pbEventCreate.progress = 80
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
}