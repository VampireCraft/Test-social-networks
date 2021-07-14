package ru.bikbulatov.comeWithMe.authorization.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.core.ui.MainActivity
import ru.bikbulatov.comeWithMe.databinding.FragmentLogInBinding

@AndroidEntryPoint
class FragmentLogIn : BaseFragment() {
    private lateinit var binding: FragmentLogInBinding
    private lateinit var viewModel: AuthorizationViewModel
    lateinit var animation: Animation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.btn_click)
        binding.tvRegistration.setOnClickListener {
            it.startAnimation(animation)
            openSignUpFragment()
        }

        binding.btnEnter.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            if (isLoginValid() && isPasswordValid())
                viewModel.logIn(
                    binding.tieLogin.rawText,
                    binding.tilPassword.editText?.text.toString()
                )
            else
                showError("Пустой логин или пароль")
        }

        binding.tvPassRecovery.setOnClickListener {
            it.startAnimation(animation)
            openRestorePasswordFragment()
        }

        viewModel.logInResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.btnEnter.isEnabled = false
                    showLoading()
                }

                Status.SUCCESS -> {
                    binding.btnEnter.isEnabled = true
                    openMainWindow()
                    hideLoading()
                }

                Status.ERROR -> {
                    binding.btnEnter.isEnabled = true
                    showError(it.error ?: "Ошибка логина")
                    hideLoading()
                }
            }
        })
    }

    private fun isLoginValid(): Boolean {
        val login = binding.tilLogin.editText?.text.toString()
        return !login.isNullOrEmpty()
    }

    private fun isPasswordValid(): Boolean {
        val password = binding.tilPassword.editText?.text.toString()
        return !password.isNullOrEmpty()
    }

    private fun openMainWindow() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    private fun openSignUpFragment() {
        parentFragmentManager
            .beginTransaction()
            .add(R.id.flAuthRoot, FragmentSignUp())
            .commit()
    }

    private fun openRestorePasswordFragment() {
        parentFragmentManager
            .beginTransaction()
            .add(R.id.flAuthRoot, RestorePasswordFragment())
            .commit()
    }

    override fun showLoading() {
        binding.pbLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding.pbLoading.visibility = View.GONE
    }

    override fun showError(error: String) {
        Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show()
    }
}