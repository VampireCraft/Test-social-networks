package ru.bikbulatov.comeWithMe.profile.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_user_info.*
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.domain.models.Gender
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.core.ui.pickers.DatePickerFragment
import ru.bikbulatov.comeWithMe.core.ui.pickers.PickListener
import ru.bikbulatov.comeWithMe.createEvent.ui.FragmentTimeMoney
import ru.bikbulatov.comeWithMe.databinding.FragmentUserInfoBinding
import ru.bikbulatov.comeWithMe.hideKeyboard
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FragmentUserInfo : BaseFragment() {

    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var viewModel: ProfileViewModel
    lateinit var userInfo: UserModel
    private lateinit var copyToCheckConformity: UserModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()
        configureRadioButtons()
        observeOnUserInfo()
        observeOnUserChange()
        configureSaveBtn()
        //configureRadioButtons()
        configureBackBtn()
        configureClickOnDate()
    }

    private fun configureClickOnDate() {
        binding.tvBirthday.setOnClickListener {
            DatePickerFragment(0, object : PickListener {
                override fun onPicked(calendar: Calendar) {
                    setDateAndTime(calendar.timeInMillis)
                    userInfo.birthday = (calendar.timeInMillis?.div(1000)).toString()
                }
            }).show(parentFragmentManager, FragmentTimeMoney::class.simpleName)
        }
    }

    private fun configureSaveBtn() {
        binding.btnNext.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            if (userInfo != copyToCheckConformity) {
                userInfo.photo = null
                viewModel.changeUserInfo(userInfo)
                it.isEnabled = false
            }
        }
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager?.popBackStack()
            binding.ivBtnBack.hideKeyboard()
        }
    }

    private fun observeOnUserChange() {
        viewModel.changeUserResponse.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    binding.pbLoading.isEnabled = true
                }
                Status.SUCCESS -> {
                    binding.pbLoading.isEnabled = false
                    binding.btnNext.isEnabled = true
                    it.data?.let { message -> showSuccessMessage(message) }
                }
                Status.ERROR -> {
                    binding.pbLoading.isEnabled = false
                    binding.btnNext.isEnabled = true
                    it.error?.let { message -> showError(message) }
                }
            }
        })
    }

    private fun observeOnUserInfo() {
        viewModel.userInfo.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("userInfo", "LOADING")
                Status.SUCCESS -> {
                    it.data?.let { userModel ->

                        userInfo = userModel
                        setUserInfoToView(userModel)
                        copyToCheckConformity = userModel.copy()
                        observeOnFieldEdit()
                    }
                }
                Status.ERROR -> Log.d("userInfo", "ERROR")
            }
        })
    }

    private fun configureRadioButtons() {
        binding.rbBoy.setOnCheckedChangeListener { it, isChecked ->
            //if (it.isPressed) {
                if (isChecked) {
                    binding.rbGirl.isChecked = !isChecked
                    binding.rbGirl.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bg_help
                        )
                    )
                    binding.rbOther.isChecked = !isChecked
                    binding.rbOther.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bg_help
                        )
                    )
                    userInfo.gender = Gender.BOY.id
                    it.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_additional
                        )
                    )
                }
            //}
        }
        binding.rbGirl.setOnCheckedChangeListener { it, isChecked ->
            //if (it.isPressed) {
                if (isChecked) {
                    binding.rbBoy.isChecked = !isChecked
                    binding.rbBoy.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bg_help
                        )
                    )
                    binding.rbOther.isChecked = !isChecked
                    binding.rbOther.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bg_help
                        )
                    )
                    userInfo.gender = Gender.GIRL.id
                    it.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_additional
                        )
                    )
                }
            //}
        }
        binding.rbOther.setOnCheckedChangeListener { it, isChecked ->
            //if (it.isPressed) {
                if (isChecked) {
                    binding.rbBoy.isChecked = !isChecked
                    binding.rbBoy.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bg_help
                        )
                    )
                    binding.rbGirl.isChecked = !isChecked
                    binding.rbGirl.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.bg_help
                        )
                    )
                    userInfo.gender = Gender.OTHER.id
                    it.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_additional
                        )
                    )
                }
            //}
        }
    }

    private fun setUserInfoToView(userModel: UserModel) {
        binding.tieName.setText(userModel.name)
        if (!userModel.birthday.isNullOrEmpty() && userModel.birthday?.toLong()!! > 0)
            setDateAndTime(userModel.birthday?.toLong()!! * 1000)
        else
            binding.tvBirthday.hint = "Дата рождения"
        binding.etAboutMe.setText(userModel.aboutMe)
        when (userModel.gender){
            1 ->{
                binding.rbBoy.isChecked = true
                binding.rbBoy.callOnClick()
            }
            2 ->{
                binding.rbGirl.isChecked = true
                binding.rbGirl.callOnClick()
            }
            3 ->{
                binding.rbOther.isChecked = true
                binding.rbOther.callOnClick()
            }
        }
    }

    private fun setDateAndTime(eventDateTime: Long?) {
        val date = Date()
        date.time = eventDateTime ?: 0
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        dateFormat.format(date)
        binding.tvBirthday.text = dateFormat.format(date)
    }

    private fun observeOnFieldEdit() {
        binding.tilName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true)
                    userInfo.name = s.toString()
            }
        })

        binding.etAboutMe.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true)
                    userInfo.aboutMe = s.toString()
            }
        })
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
}

