package ru.bikbulatov.comeWithMe.profile.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.databinding.FragmentContactsBinding
import ru.bikbulatov.comeWithMe.hideKeyboard
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel

@AndroidEntryPoint
class FragmentContacts : BaseFragment() {

    private lateinit var binding: FragmentContactsBinding
    private lateinit var viewModel: ProfileViewModel

    lateinit var userInfo: UserModel
    lateinit var copyToCheckConformity: UserModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()
        observeOnUserChange()
        observeOnUserInfo()
        configureBackBtn()
        configureSaveBtn()
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
            binding.ivBtnBack.hideKeyboard()
        }
    }

    private fun observeOnUserChange() {
        viewModel.changeUserResponse.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> Log.d("changeUserResponse", "LOADING")
                Status.SUCCESS -> {
                    it.data?.let { message -> showSuccessMessage(message) }
                }
                Status.ERROR -> it.error?.let { message -> showError(message) }
            }
        })
    }

    private fun observeOnUserInfo() {
        viewModel.userInfo.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("userInfo", "LOADING")
                Status.SUCCESS -> {
                    it.data?.let { userModel ->
                        setUserInfoToView(userModel)
                        userInfo = userModel
                        copyToCheckConformity = userModel.copy()
                        observeOnFieldEdit()
                    }
                }
                Status.ERROR -> Log.d("userInfo", "ERROR")
            }
        })
    }

    private fun setUserInfoToView(userModel: UserModel) {
        binding.tilEmail.editText?.setText(userModel.email)
        binding.tilPhone.editText?.setText(userModel.phone)
        binding.tilInstagram.editText?.setText(userModel.instagram)
        binding.tilVk.editText?.setText(userModel.vk)
        binding.tilTelegram.editText?.setText(userModel.telegram)
    }

    private fun observeOnFieldEdit() {
        binding.etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true)
                    userInfo.phone = binding.etPhoneNumber.rawText
                else
                    userInfo.phone = ""
            }
        })

        binding.tieEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true)
                    userInfo.email = s.toString()
                else
                    userInfo.email = ""
            }
        })

        binding.tieInstagram.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true)
                    userInfo.instagram = s.toString()
                else
                    userInfo.instagram = ""
            }
        })

        binding.tieVk.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true)
                    userInfo.vk = s.toString()
                else
                    userInfo.vk = ""
            }
        })

        binding.tieTelegram.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true)
                    userInfo.telegram = s.toString()
                else
                    userInfo.telegram = ""
            }
        })
    }

    private fun configureSaveBtn() {
        binding.btnNext.setOnClickListener {
            if (userInfo != copyToCheckConformity)
                userInfo.photo = null
                viewModel.changeUserInfo(userInfo)
        }
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(error: String) {

    }
}