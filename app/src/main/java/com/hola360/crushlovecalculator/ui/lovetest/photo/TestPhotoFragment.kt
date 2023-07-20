package com.hola360.crushlovecalculator.ui.lovetest.photo

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.databinding.FragmentTestPhotoBinding
import com.hola360.crushlovecalculator.dialog.crop.CropPhotoDialog
import com.hola360.crushlovecalculator.dialog.photopicker.PickPhotoDialog
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.base.BaseLoveTest
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.DataBindingUtils.setLoveTestPhoto
import com.hola360.crushlovecalculator.utils.LoveTestUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.Utils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.FileNotFoundException

class TestPhotoFragment : BaseLoveTest<FragmentTestPhotoBinding>(), View.OnClickListener,
    PickPhotoDialog.OnPhotoPickListener {

    private fun pickPhoto() {
        val pickPhotoDialog = PickPhotoDialog.create()
        pickPhotoDialog.onPhotoPickListener = this
        pickPhotoDialog.show(parentFragmentManager, "PickPhoto")
    }


    override fun initView() {
        getTestStringResult = false
        binding!!.toolbar.back.setOnClickListener(this)
        binding!!.toolbar.toolbarTitle.text = getString(R.string.love_test_photo_title)
        binding!!.yourProfile = yourProfileModel
        binding!!.crushProfile = crushProfileModel
        binding!!.yourPhoto.setOnClickListener(this)
        binding!!.crushPhoto.setOnClickListener(this)
        binding!!.changeYourPhoto.setOnClickListener(this)
        binding!!.changeCrushPhoto.setOnClickListener(this)
        binding!!.startTest.setOnClickListener(this)
        binding!!.edtYourName.setText(yourProfileModel.name)
        binding!!.edtYourName.addTextChangedListener(edtYourNameChangeListener)
        binding!!.edtCrushName.addTextChangedListener(edtCrushNameChangeListener)
        KeyboardVisibilityEvent.setEventListener(mainActivity, this) {
            if (it) {
                binding!!.appBarLayout.setExpanded(false)
            } else {
                binding!!.appBarLayout.setExpanded(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateYourProfile()
        updateCrushProfile()
    }

    override fun updateYourProfile() {
        binding!!.yourProfile = yourProfileModel
        binding!!.yourPhoto.setLoveTestPhoto(yourProfileModel.avatarUrl)
    }

    override fun updateCrushProfile() {
        binding!!.crushProfile = crushProfileModel
        binding!!.crushPhoto.setLoveTestPhoto(crushProfileModel.avatarUrl)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                findNavController().popBackStack()
            }
            R.id.yourPhoto, R.id.changeYourPhoto -> {
                youPick = true
                checkStoragePermission()
            }
            R.id.crushPhoto, R.id.changeCrushPhoto -> {
                youPick = false
                checkStoragePermission()
            }
            R.id.startTest -> {
                checkBeforeStartTest()
            }

        }
    }


    private fun checkBeforeStartTest() {
        if (yourProfileModel.avatarUrl == null || crushProfileModel.avatarUrl == null) {
            mainActivity.showSnackBar(
                SnackBarType.Error,
                getString(R.string.love_test_photo_error),
                getString(R.string.love_test_photo_no_photo)
            )
            if (yourProfileModel.avatarUrl == null) {
                binding!!.yourPhoto.setImageResource(R.drawable.ic_lovetest_photo_default_error)
            }
            if (crushProfileModel.avatarUrl == null) {
                binding!!.crushPhoto.setImageResource(R.drawable.ic_lovetest_photo_default_error)
            }
            return
        } else {
            if (SystemUtils.checkConnection(requireContext())) {
                testResult = LoveTestUtils.getPhotoMatch(
                    requireContext(),
                    yourProfileModel.avatarUrl!!,
                    crushProfileModel.avatarUrl!!
                )
                SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
                navigateToAnimation(LoveTestFragment.PHOTO_TYPE)
            } else {
                showNetworkWarning()
            }
        }
    }


    private fun checkStoragePermission() {
        if (!SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())) {
            storageResultLauncher.launch(SystemUtils.getStoragePermissions())
        } else {
            pickPhoto()
        }
    }

    private val storageResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (SystemUtils.hasPermissions(
                    requireContext(),
                    *SystemUtils.getStoragePermissions()
                )
            ) {
                pickPhoto()
            } else {
                SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
            }
        }

    override fun getLayout(): Int {
        return R.layout.fragment_test_photo
    }

    override fun onPicked(photoModel: PhotoModel?) {
        if (photoModel != null) {
            val cropPhotoDialog = CropPhotoDialog.create(
                photoModel.uri,
                isFromMediaStore = true,
                isSquare = true
            , isRectangle = false)
            cropPhotoDialog.setCroppedImageSize(Constants.TEST_PHOTO_CROP_HEIGHT, Constants.TEST_PHOTO_CROP_WIDTH)
            cropPhotoDialog.onCropDoneListener = object : CropPhotoDialog.OnCropDoneListener {
                override fun onCropDone(uri: Uri?) {
                    if (uri != null) {
                        if (youPick) {
                            yourProfileModel.avatarUrl = uri.toString()
                            updateYourProfile()
                        } else {
                            crushProfileModel.avatarUrl = uri.toString()
                            updateCrushProfile()
                        }
                    }
                }
            }
            cropPhotoDialog.show(parentFragmentManager, "CropImage")
        }

    }
}