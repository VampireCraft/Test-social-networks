package ru.bikbulatov.comeWithMe.authorization.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.databinding.FragmentRestorePasswordBinding

@AndroidEntryPoint
class RestorePasswordFragment : Fragment() {

    private lateinit var binding: FragmentRestorePasswordBinding
    private lateinit var viewModel: AuthorizationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestorePasswordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogin.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .add(R.id.flAuthRoot, FragmentLogIn())
                .commit()
        }

        binding.btnEnter.setOnClickListener {
            viewModel.restorePassword(binding.tieLogin.text.toString())
        }

        viewModel.restorePasswordResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    parentFragmentManager
                        .beginTransaction()
                        .add(R.id.flAuthRoot, RestorePasswordSmsFragment())
                        .commit()
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}