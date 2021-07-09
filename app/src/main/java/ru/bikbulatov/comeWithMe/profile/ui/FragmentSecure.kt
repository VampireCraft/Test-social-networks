package ru.bikbulatov.comeWithMe.profile.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.databinding.FragmentSecureBinding
import ru.bikbulatov.comeWithMe.hideKeyboard

@AndroidEntryPoint
class FragmentSecure : BaseFragment() {

    private lateinit var binding: FragmentSecureBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecureBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBackBtn()

        binding.btnNext.setOnClickListener {
            if (binding.tiePassword.text.toString() != binding.tieRepeatPassword.text.toString()){
                Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.changePassword(binding.tiePassword.text.toString())
            }
        }

        viewModel.changePassword.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("changePassword", "LOADING")
                Status.SUCCESS -> {
                    Toast.makeText(requireContext(), "Пароль изменен", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR -> it.error?.let { message -> showError(message) }
            }
        })
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
            binding.ivBtnBack.hideKeyboard()
        }
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun showError(error: String) {
    }
}