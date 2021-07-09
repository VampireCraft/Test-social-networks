package ru.bikbulatov.comeWithMe.authorization.ui.steps

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.core.ui.MainActivity
import ru.bikbulatov.comeWithMe.databinding.FragmentSignUpFifthStepBinding

@AndroidEntryPoint
class FragmentFifthStep : BaseFragment() {
    private lateinit var binding: FragmentSignUpFifthStepBinding
    private lateinit var viewModel: AuthorizationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpFifthStepBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(requireParentFragment()).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBtnNext()
        observeOnSignUpResponse()
        configureTermsOfUse()

        binding.tilPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.password = s.toString()
            }
        })
    }

    private fun configureTermsOfUse() {
        binding.tvTermsOfUse.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun configureBtnNext() {
        binding.btnNext.setOnClickListener {
            if (isPasswordValidOrShowError() && isPasswordsMatchOrShowError()) {
                viewModel.signUp(binding.tilPassword.editText?.text.toString())
            }
        }
    }

    private fun observeOnSignUpResponse() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    //todo
//                    showLoading()
                }

                Status.SUCCESS -> {
                    Toast.makeText(
                        requireActivity(),
                        "Регистрация прошла успешна",
                        Toast.LENGTH_SHORT
                    ).show()
                    it.data?.refresh?.let { refreshToken ->
                        TokenRepository.saveTokens(it.data.access, refreshToken)
                    }
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                Status.ERROR -> {
                    showError(it.error ?: "error")
                }
            }
        })
    }

    private fun isPasswordValidOrShowError(): Boolean {
        return if (!viewModel.password?.isNullOrEmpty() && viewModel.password.length > 3) {
            binding.tilPassword.error = ""
            true
        } else {
            binding.tilPassword.error = "Пароль должен содержать не менее 4-ех символов"
            false
        }
    }

    private fun isPasswordsMatchOrShowError(): Boolean {
        return if (binding.tilPassword.editText?.text?.toString()
                .equals(binding.tilPasswordConfirm.editText?.text.toString())
        ) {
            binding.tilPasswordConfirm.error = ""
            true
        } else {
            binding.tilPasswordConfirm.error = "Пароли должны совпадать"
            false
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(error: String) {
        Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show()
    }
}