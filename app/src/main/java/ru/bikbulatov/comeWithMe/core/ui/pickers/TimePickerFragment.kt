package ru.bikbulatov.comeWithMe.core.ui.pickers

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*


class TimePickerFragment(private val pickListener: PickListener) : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    private lateinit var calendar: Calendar

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        pickListener.onPicked(calendar)
    }
}