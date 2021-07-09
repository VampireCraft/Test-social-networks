package ru.bikbulatov.comeWithMe.profile.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.ui.AuthActivity
import ru.bikbulatov.comeWithMe.core.domain.DialogAction
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.network.TokenRepository
import ru.bikbulatov.comeWithMe.core.ui.TwoButtonsDialog
import ru.bikbulatov.comeWithMe.databinding.FragmentProfileBinding
import ru.bikbulatov.comeWithMe.profile.ChangeProfilePhotoDialog
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel
import java.io.ByteArrayOutputStream
import java.io.File

private const val IMAGE = "IMAGE"

@AndroidEntryPoint
class FragmentProfile : Fragment(), ChangeProfilePhotoDialog.Callback {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var easyImage: EasyImage
    private lateinit var rxPermissions: RxPermissions
    private lateinit var userModel: UserModel
    private var userPhoto = ""

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        easyImage = EasyImage.Builder(requireContext())
            .setCopyImagesToPublicGalleryFolder(false) // Sets the name for images stored if setCopyImagesToPublicGalleryFolder = true
            .setFolderName("EasyImage sample") // Allow multiple picking
            .allowMultiple(true)
            .build()

        rxPermissions = RxPermissions(this)
        rxPermissions
            .request(Manifest.permission.CAMERA)
            .subscribe {

            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()
        observeOnUserInfo()
        configureExitBtn()
        configureUserInfoBtn()
        configureContactsBtn()
        configureSecureBtn()

        initClickers()
    }

    private fun initClickers() {
        binding.ivUser.setOnClickListener {
            rxPermissions
                .request(Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (granted) {
                        val dialog = ChangeProfilePhotoDialog()
                        dialog.setCallback(this)
                        dialog.show(parentFragmentManager, "ChangeProfilePhotoDialog")
                    }
                }
        }
    }

    private fun configureContactsBtn() {
        binding.tvContacts.setOnClickListener {
            childFragmentManager
                .beginTransaction()
                .add(binding.root.id, FragmentContacts())
                .addToBackStack(FragmentContacts::class.simpleName)
                .commit()
        }
    }

    private fun configureSecureBtn() {
        binding.tvSecure.setOnClickListener {
            childFragmentManager
                .beginTransaction()
                .add(binding.root.id, FragmentSecure())
                .addToBackStack(FragmentSecure::class.simpleName)
                .commit()
        }
    }

    private fun configureUserInfoBtn() {
        binding.tvUserInfo.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .add(binding.root.id, FragmentUserInfo())
                .addToBackStack(FragmentUserInfo::class.simpleName)
                .commit()
        }
    }

    private fun configureExitBtn() {
        binding.clExit.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            TwoButtonsDialog(
                positiveBtnText = "Выйти",
                negativeTextBtn = "Остаться",
                title = "Ты уверен, что хочешь выйти из аккаунта?",
                action = object : DialogAction {
                    override fun onPositiveBtnClicked() {
                        TokenRepository.deleteTokens()
                        val intent = Intent(requireContext(), AuthActivity::class.java)
                        requireActivity().startActivity(intent)
                        requireActivity().finish()
                    }

                    override fun onNegativeBtnClicked() {

                    }

                }
            ).show(parentFragmentManager, TwoButtonsDialog::class.simpleName)
        }
    }

    private fun observeOnUserInfo() {
        viewModel.userInfo.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> Log.d("userInfo", "LOADING")
                Status.SUCCESS -> {
                    it.data?.let {
                        setUserInfoToView(it)
                    }
                }
                Status.ERROR -> Log.d("userInfo", "ERROR")
            }
        })
    }

    private fun setUserInfoToView(userModel: UserModel) {
        binding.tvUserRating.text = userModel.totalRating.toString()
        this.userModel = userModel
        loadUserPhoto(userModel.photo)
    }

    /** Transform phone
     * @phone  7xxxxxxxxxx or xxxxxxxxxx
     * @return +7 (xxx) xxx-xx-xx
     */
    fun transformPhoneToUser(phone: String?): String {
        if (phone == null || phone == "") return ""
        val regex = Regex("(\\d{3})(\\d{3})(\\d{2})(\\d+)")
        return if (phone[0] == '7' || phone[0] == '8') {
            "+7 " + phone.substring(1).replaceFirst(regex, "($1) $2-$3-$4")
        } else {
            "+7 " + phone.replaceFirst(regex, "($1) $2-$3-$4")
        }
    }

    private fun loadUserPhoto(url: String?) {
        userPhoto = url.toString()
        Glide
            .with(requireContext())
            .load(url)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.ic_default_profile_photo)
            .into(binding.ivUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(
            requestCode,
            resultCode,
            data,
            requireActivity(),
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    Glide
                        .with(requireContext())
                        .load(imageFiles[0].file)
                        .centerCrop()
                        .apply(RequestOptions().circleCrop())
                        .placeholder(R.drawable.ic_default_profile_photo)
                        .into(binding.ivUser)
                    lifecycleScope.launch {
                        val compressedImageFile =
                            Compressor.compress(requireContext(), imageFiles[0].file) {
                                resolution(1280, 720)
                                quality(80)
                                format(Bitmap.CompressFormat.JPEG)
                            }
                        userModel.photo = convertImageFileToBase64(compressedImageFile)
                        viewModel.changeUserInfo(userModel)
                    }
                }

                override fun onImagePickerError(
                    @NonNull error: Throwable,
                    @NonNull source: MediaSource
                ) {
                    error.printStackTrace()
                }

                override fun onCanceled(@NonNull source: MediaSource) {

                }
            })
    }

    fun convertImageFileToBase64(imageFile: File): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                imageFile.inputStream().use { inputStream ->
                    inputStream.copyTo(base64FilterStream)
                }
            }
            return@use outputStream.toString()
        }
    }

    override fun changePhoto() {
        easyImage.openChooser(this)
    }

    override fun openPhoto() {
        if (userPhoto.isNotEmpty()){
            val fullImageIntent = Intent(requireActivity(), ImageViewerActivity::class.java)
            fullImageIntent.putExtra(IMAGE, userPhoto)
            startActivity(fullImageIntent)
        }
    }

    override fun deletePhoto() {

    }

}