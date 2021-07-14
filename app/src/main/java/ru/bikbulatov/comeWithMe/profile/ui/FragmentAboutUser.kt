package ru.bikbulatov.comeWithMe.profile.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.domain.models.Gender
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.databinding.FragmentAboutUserBinding
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel


class FragmentAboutUser : BaseFragment() {

    companion object {
        fun createInstance(userId: Int): FragmentAboutUser {
            return FragmentAboutUser().apply {
                arguments = Bundle().apply {
                    putInt("userId", userId)
                }
            }
        }
    }

    private lateinit var binding: FragmentAboutUserBinding
    private lateinit var viewModel: ProfileViewModel

    var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutUserBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserIdFromArguments()
        observeOnUserInfo()
        configureBackBtn()
        configureFeeBackBtn()
        viewModel.getUserInfoAndFeedBacks(userId)
    }

    private fun configureFeeBackBtn() {
        binding.btnNext.setOnClickListener {
            FeedBackDialog().show(childFragmentManager, FeedBackDialog::class.simpleName)
        }
    }

    private fun getUserIdFromArguments() {
        userId = requireArguments().getInt("userId")
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager?.popBackStack()
        }
    }

    private fun observeOnUserInfo() {
        viewModel.userInfoAndFeedBacks.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("userInfoAndFeedBacks", "LOADING")
                Status.SUCCESS -> {
                    Log.d("userInfoAndFeedBacks", "SUCCESS")
                    it.data?.let { userModel ->
                        setUserInfoToView(userModel)
                    }
                }
                Status.ERROR -> Log.d("userInfoAndFeedBacks", "ERROR")
            }
        })
    }

    private fun setUserInfoToView(userModel: UserModel) {
        binding.tvUserName.text = userModel.name
        binding.tvAboutUser.text = userModel.aboutMe
        binding.tvUserRating.text = userModel.totalRating.toString()
        binding.tvUserPhone.text =
            "+7(" + userModel.phone?.take(3) + ")" + userModel.phone?.substring(3)

        when (userModel.gender){
            1 -> {
                binding.tvGender.text = "Парень"
            }
            2 -> {
                binding.tvGender.text = "Девушка"
            }
            3 -> {
                binding.tvGender.text = "Не определился"
            }
        }

        if (!userModel.instagram.isNullOrEmpty()) {
            binding.ivInstagram.setOnClickListener {
                val appUri: Uri? =
                    Uri.parse(
                        "https://instagram.com/_u/${
                            userModel.instagram!!.replace("@", "")
                        }"
                    )
                val browserUri: Uri? =
                    Uri.parse(
                        "https://instagram.com/${
                            userModel.instagram!!.replace("@", "")
                        }"
                    )
                try {
                    val appIntent =
                        requireActivity().packageManager.getLaunchIntentForPackage(
                            "com.instagram.android"
                        )
                    if (appIntent != null) {
                        appIntent.action = Intent.ACTION_VIEW
                        appIntent.data = appUri
                        startActivity(appIntent)
                    }
                } catch (e: Exception) { //or else open in browser
                    val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                    startActivity(browserIntent);
                }
            }
            Glide.with(requireView())
                .load(R.drawable.ic_instagram_big)
                .into(binding.ivInstagram)
        }
        if (!userModel.vk.isNullOrEmpty()) {
            binding.ivVk.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://vk.com/${userModel.vk}")
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {

                }
            }
            Glide.with(requireView())
                .load(R.drawable.ic_vk_big)
                .into(binding.ivVk)
        }
        if (!userModel.telegram.isNullOrEmpty()) {
            binding.ivTelegram.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "tg://resolve?domain=${
                            userModel.telegram!!.replace("@", "")
                        }"
                    )
                )
                startActivity(intent)
            }
            Glide.with(requireView())
                .load(R.drawable.ic_telegram_big)
                .into(binding.ivTelegram)
        }
        loadUserPhoto(binding.ivUser, userModel.photo)
    }

    private fun loadUserPhoto(imageView: ImageView, url: String?) {
        Glide
            .with(imageView.context)
            .load(url)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.ic_default_profile_photo)
            .into(imageView)
    }

    fun showSuccessMessage(message: String) {
        Toast.makeText(requireContext(), "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(error: String) {
        Toast.makeText(requireContext(), "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show()
    }
}