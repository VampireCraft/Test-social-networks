package ru.bikbulatov.comeWithMe.authorization.ui.steps

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.authorization.ui.vm.AuthorizationViewModel
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.databinding.FragmentSignUpFourthStepBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream


@AndroidEntryPoint
class FragmentFourthStep : BaseFragment() {
    private lateinit var binding: FragmentSignUpFourthStepBinding
    private lateinit var viewModel: AuthorizationViewModel

    companion object {
        private const val ATTACHMENTS_FROM_GALLERY = 0
        private const val ATTACHMENTS_FROM_CAMERA = 1
        private const val SELECT_PICTURE_FROM_CAMERA = 5
        private const val SELECT_PICTURE_FROM_GALLERY = 6
    }

    private val CAMERA_PERMISSION_STORAGE = 999
    private val GALERY_PERMISSION_STORAGE = 1001
    private val REQUEST_CODE_CAMERA_PERMISSION = 1003
    private val REQUEST_CODE_GALERY_PERMISSION = 1004

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpFourthStepBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(requireParentFragment()).get(AuthorizationViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNextBtn()
        configureLoadPhotoBtn()
    }

    private fun configureNextBtn() {
        binding.btnNext.setOnClickListener {
            viewModel.stepsNavigator.openFifthStep()
        }
    }

    private fun configureLoadPhotoBtn() {
        binding.ivLoadPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) == true) {
            AlertDialog.Builder(activity)
                .setItems(
                    R.array.view_types_dialog_with_camera,
                    fun(_: DialogInterface, attachmentId: Int) {
                        when (attachmentId) {
                            ATTACHMENTS_FROM_GALLERY ->
                                if (!checkReadExternalPermission()) {
                                    requestReadExternalPermission()
                                } else
                                    getImageFromAlbum()
                            ATTACHMENTS_FROM_CAMERA ->
                                openCameraIntent()
                        }
                    })
                .show()
        } else {
            getImageFromAlbum()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE_FROM_CAMERA) {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                binding.ivLoadPhoto.setImageBitmap(imageBitmap)
            } else if (requestCode == SELECT_PICTURE_FROM_GALLERY) {
                try {
                    val imageUri: Uri? = data?.data
                    var imageStream: InputStream? = null
                    if (imageUri != null)
                        imageStream =
                            activity?.contentResolver?.openInputStream(imageUri)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    binding.ivLoadPhoto.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
//                    Toast.makeText(this@PostImage, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_STORAGE) {
            if (checkReadExternalPermission() && checkCameraPermission())
                openCameraIntent()
//            else
//                UtilsUI.CoolToast("Для передачи изображений с камеры, необходим доступ к хранилищу и камере", Constants.TOAST_WARNING)
        }
        if (requestCode == GALERY_PERMISSION_STORAGE) {
            if (checkReadExternalPermission()) getImageFromAlbum()
//            else
//                UtilsUI.CoolToast("Для передачи изображений, необходим доступ к хранилищу", Constants.TOAST_WARNING)
        }
    }

    private fun getImageFromAlbum() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, SELECT_PICTURE_FROM_GALLERY)
    }

    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
//            var photoFile: File? = null
//            try {
//                photoFile = createImageFile()
//            } catch (ex: IOException) {
//            }
//            if (photoFile != null) {
//                val photoURI = FileProvider.getUriForFile(
//                    requireActivity(),
//                    "ru.ufanet.myufanet.provider",
//                    photoFile
//                )
//                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                if (ContextCompat.checkSelfPermission(
//                        requireActivity(),
//                        Manifest.permission.CAMERA
//                    ) == PackageManager.PERMISSION_DENIED
//                ) {
//                    requestPermissions(
//                        arrayOf(Manifest.permission.CAMERA),
//                        REQUEST_CODE_CAMERA_PERMISSION
//                    )
//                } else {
        startActivityForResult(pictureIntent, SELECT_PICTURE_FROM_CAMERA)
//                }
//            }
//        }
    }

    private fun createImageFile(): File {
        val imageFileName = "IMG_" + "_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
//        imageFilePath = image.absolutePath
        return image
    }

    private fun checkReadExternalPermission(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val res = requireContext().checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadExternalPermission() {
        this.requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            GALERY_PERMISSION_STORAGE
        )
    }

    private fun checkCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        val res = requireContext().checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        this.requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ), CAMERA_PERMISSION_STORAGE
        )
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }

    override fun showError(error: String) {
        TODO("Not yet implemented")
    }
}