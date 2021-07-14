package ru.bikbulatov.comeWithMe.authorization.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.databinding.FragmentRestorePasswordSmsBinding

@AndroidEntryPoint
class RestorePasswordSmsFragment : Fragment() {

    private lateinit var binding: FragmentRestorePasswordSmsBinding
    private lateinit var viewModel: AuthorizationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestorePasswordSmsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tieCode.addTextChangedListener {
            if (it.toString().length == 4){
                viewModel.sendSmsCode(it.toString())
                viewModel.code = it.toString()
            }
        }

        viewModel.smsCodeResponse.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.tvErrorMessage.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    binding.tvErrorMessage.visibility = View.GONE
                    parentFragmentManager
                        .beginTransaction()
                        .add(R.id.flAuthRoot, EnterRestorePasswordFragment())
                        .commit()
                }
                Status.ERROR -> {
                    binding.tvErrorMessage.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}