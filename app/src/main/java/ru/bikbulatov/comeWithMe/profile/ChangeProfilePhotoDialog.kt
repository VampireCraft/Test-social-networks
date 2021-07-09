package ru.bikbulatov.comeWithMe.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.bikbulatov.comeWithMe.databinding.DialogChangeProfilePhotoBinding
import ru.bikbulatov.comeWithMe.profile.ui.FragmentProfile

class ChangeProfilePhotoDialog : DialogFragment() {

    private lateinit var binding: DialogChangeProfilePhotoBinding
    private lateinit var callback: Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChangeProfilePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitleChangePhoto.setOnClickListener {
            callback.changePhoto()
            dismiss()
        }
        binding.tvTitleOpenPhoto.setOnClickListener {
            callback.openPhoto()
            dismiss()
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {

        fun changePhoto()

        fun openPhoto()

        fun deletePhoto()
    }
}