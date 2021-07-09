package ru.bikbulatov.comeWithMe.core.ui.pickers

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val startDate: Long = 0, private val pickListener: PickListener) :
    DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    private lateinit var calendar: Calendar

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, year, month, day).apply {
            datePicker.minDate = startDate
            datePicker.maxDate = calendar.apply { add(Calendar.YEAR, 1) }.timeInMillis
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        pickListener.onPicked(calendar)
    }
}