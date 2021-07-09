package ru.bikbulatov.comeWithMe.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.bikbulatov.comeWithMe.core.domain.DialogAction
import ru.bikbulatov.comeWithMe.databinding.DialogTwoButtonsBinding

class TwoButtonsDialog(
    val positiveBtnText: String,
    val negativeTextBtn: String,
    val title: String,
    val action: DialogAction
) : DialogFragment() {

    private lateinit var binding: DialogTwoButtonsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTwoButtonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPositive.text = positiveBtnText
        binding.btnNegative.text = negativeTextBtn
        binding.tvTitle.text = title
        binding.btnPositive.setOnClickListener {
            action.onPositiveBtnClicked()
            dialog?.dismiss()
        }

        binding.btnNegative.setOnClickListener {
            action.onNegativeBtnClicked()
            dialog?.dismiss()
        }
    }
}