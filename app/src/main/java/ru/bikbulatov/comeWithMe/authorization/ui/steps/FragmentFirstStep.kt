package ru.bikbulatov.comeWithMe.authorization.ui.steps

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.databinding.FragmentSignUpFirstStepBinding
import java.util.regex.Pattern

@AndroidEntryPoint
class FragmentFirstStep : BaseFragment() {
    private lateinit var binding: FragmentSignUpFirstStepBinding
    private lateinit var viewModel: AuthorizationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpFirstStepBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(requireParentFragment()).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        reloadDataFromViewModel()
        configureNextBtn()
        observeOnFieldsChanges()
    }

    private fun observeOnFieldsChanges() {
        binding.tilName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.userName = s.toString()
            }
        })

//        binding.tilEmail.editText?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                viewModel.email = s.toString()
//            }
//        })

        binding.etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.phoneNumber = binding.etPhoneNumber.rawText
            }
        })
    }

    private fun configureNextBtn() {
        binding.btnNext.setOnClickListener {
            if (isNameValidOrShowError() && isPhoneValidOrShowError()) {
                viewModel.stepsNavigator.openThirdStep()
                hideKeyboard()
            }
        }
    }

    private fun reloadDataFromViewModel() {
        binding.tilPhone.editText?.setText(viewModel.phoneNumber)
        binding.tilEmail.editText?.setText(viewModel.email)
        binding.tilName.editText?.setText(viewModel.userName)
    }

    private fun isNameValidOrShowError(): Boolean {
        return if (!viewModel.userName?.isNullOrEmpty() && viewModel.userName.trim().length > 3) {
            binding.tilName.error = ""
            true
        } else {
            binding.tilName.error = "Имя должно содержать не менее 4-ех символов"
            false
        }
    }

    private fun isEmailValidOrShowError(): Boolean {
        return when {
            viewModel.email?.isNullOrEmpty() -> {
                binding.tilEmail.error = "Введите email"
                false
            }
            !isEmailPatternValid(viewModel.email) -> {
                binding.tilEmail.error = "Некорректный email"
                false
            }
            else -> {
                binding.tilEmail.error = ""
                true
            }
        }
    }

    private fun isPhoneValidOrShowError(): Boolean {
        return if (!viewModel.phoneNumber?.isNullOrEmpty()) {
            binding.tilPhone.error = ""
            true
        } else {
            binding.tilPhone.error = "Введите номер телефона"
//            showError("Введите номер телефона")
            false
        }
    }

    fun isEmailPatternValid(email: String): Boolean {
        val regExpn = ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")
        val inputStr: CharSequence = email
        val pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(inputStr)
        return matcher.matches()
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }

    override fun showError(error: String) {
        Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(
                this.requireView().applicationWindowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
    }

}