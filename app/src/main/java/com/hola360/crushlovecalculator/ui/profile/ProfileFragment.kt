package com.hola360.crushlovecalculator.ui.profile

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.data.model.ProfileModel
import com.hola360.crushlovecalculator.databinding.FragmentProfileBinding
import com.hola360.crushlovecalculator.dialog.action.PickActionDiaLog
import com.hola360.crushlovecalculator.dialog.action.model.ActionModel
import com.hola360.crushlovecalculator.dialog.crop.CropPhotoDialog
import com.hola360.crushlovecalculator.dialog.jobdialog.JobDialog
import com.hola360.crushlovecalculator.dialog.lovetimedialog.DatePickerDiaLog
import com.hola360.crushlovecalculator.dialog.nationdialog.NationDialog
import com.hola360.crushlovecalculator.dialog.nationdialog.NationModel
import com.hola360.crushlovecalculator.dialog.nationdialog.NationUtils
import com.hola360.crushlovecalculator.dialog.nationdialog.NationUtils.loadFlag
import com.hola360.crushlovecalculator.dialog.photopicker.PickPhotoDialog
import com.hola360.crushlovecalculator.dialog.unitpopup.ItemUnitAdapter
import com.hola360.crushlovecalculator.dialog.unitpopup.UnitPopup
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.DataBindingUtils.setTestBirthdayTime
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.SystemUtils.getStoragePermissions
import com.hola360.crushlovecalculator.utils.Utils

class ProfileFragment : AbsBaseFragment<FragmentProfileBinding>(), View.OnClickListener,
    DatePickerDiaLog.OnDatePickerListener, PickActionDiaLog.OnActionPickerListener,
    PickPhotoDialog.OnPhotoPickListener, TakePicture.SetImageToCamera {
    private val actionIcons: ArrayList<Int> by lazy {
        arrayListOf(R.drawable.ic_lovetime_dialog_camera, R.drawable.ic_lovetime_dialog_gallery)
    }
    private val actionTitle: ArrayList<String> by lazy {
        arrayListOf(
            getString(R.string.profile_avatar_dialog_camera),
            getString(R.string.profile_avatar_dialog_gallery)
        )
    }
    private val actionModelList: List<ActionModel> by lazy {
        val actions = actionIcons.flatMapIndexed { index, item ->
            val actionsList = arrayListOf(ActionModel(item, actionTitle[index], false))
            actionsList
        }
        actions
    }
    private var showDialog = false
    private val args: ProfileFragmentArgs by lazy {
        ProfileFragmentArgs.fromBundle(requireArguments())
    }
    private val profileModel: ProfileModel by lazy {
        dataPref.getUserProfileModel(args.isCrush)
    }


    private fun pickPhoto() {
        val pickPhotoDialog = PickPhotoDialog.create()
        pickPhotoDialog.onPhotoPickListener = this
        pickPhotoDialog.show(parentFragmentManager, "PickPhoto")
    }


    private val pickBirthdayDialog: DatePickerDiaLog by lazy {
        DatePickerDiaLog.create(
            if (args.isCrush) {
                getString(R.string.love_test_birthday_crush_date)
            } else {
                getString(R.string.love_test_birthday_your_date)
            }, true
        )
    }

    private val nations: MutableList<NationModel> by lazy {
        NationUtils.generateNationList(requireContext())
    }
    private val nationDialog: NationDialog by lazy {
        NationDialog.create(object : NationDialog.NationDialogCallback {
            override fun onSelectItem(nationCode: String) {
                val nation = nations.find { it.nationInformation?.code!! == nationCode }
                profileModel.nation = nation
                binding!!.nationFlag.loadFlag(nation!!)
                binding!!.txtCountry.text = nation.nationInformation!!.name
            }

            override fun onDismiss() {
                showDialog = false
            }
        })
    }
    private val jobList: MutableList<String> by lazy {
        JobDialog.getListJob(requireContext())
    }
    private val jobDialog: JobDialog by lazy {
        JobDialog.create(object : NationDialog.NationDialogCallback {
            override fun onSelectItem(job: String) {
                if (job.trim().lowercase() != "other") {
                    profileModel.job = job
                    binding!!.edtJob.setText(job)
                } else {
                    binding!!.edtJob.requestFocus()
                    binding!!.edtJob.text?.length?.let { binding!!.edtJob.setSelection(it) }
                    SystemUtils.showSoftKeyboard(requireContext(), binding!!.root)
                }
            }

            override fun onDismiss() {
                showDialog = false
            }
        })
    }
    private val unitPopupDialog: UnitPopup by lazy {
        UnitPopup(requireContext())
    }

    override fun initView() {
        binding!!.toolbar.setNavigationIcon(R.drawable.ic_toolbar_back)
        binding!!.toolbar.setNavigationOnClickListener { onBackPress() }
        binding!!.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_done) {
                if (!showDialog) {
                    saveChangeProfile()
                }
            }
            true
        }

        binding!!.isCrush = args.isCrush
        binding!!.update.setOnClickListener(this)
        binding!!.avatar.setOnClickListener(this)
        binding!!.takePicture.setOnClickListener(this)
        binding!!.defaultImage.setOnClickListener(this)
        binding!!.nationFlag.setOnClickListener(this)
        binding!!.txtCountry.setOnClickListener(this)
        binding!!.txtSelectJob.setOnClickListener(this)
        binding!!.pickDate.setOnClickListener(this)
        binding!!.txtBirthday.setOnClickListener(this)
        binding!!.txtHeightUnit.setOnClickListener(this)
        binding!!.txtWeightUnit.setOnClickListener(this)
        binding!!.edtJob.doOnTextChanged { text, start, before, count ->
            if (text != null) {
                profileModel.job = if (text.trim().isEmpty()) {
                    null
                } else {
                    val job = text.trim().substring(0, 1).uppercase() + text.trim().substring(1)
                    job
                }
            }
        }

        nationDialog.setDialogCurOwnerId(R.id.nav_profile)
        jobDialog.setDialogCurOwnerId(R.id.nav_profile)
        setTextUnit()
        binding!!.profile = profileModel

    }

    private fun onBackPress() {
        if (!showDialog) {
            findNavController().popBackStack()
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
        }
    }

    private fun setTextUnit() {
        val heightUnits = resources.getStringArray(R.array.height_unit).toMutableList()
        val weightUnits = resources.getStringArray(R.array.weight_unit).toMutableList()
        binding!!.txtHeightUnit.text = if (profileModel.heightUnitCm) {
            heightUnits[0]
        } else {
            heightUnits[1]
        }
        binding!!.txtWeightUnit.text = if (profileModel.weightUnitKg) {
            weightUnits[0]
        } else {
            weightUnits[1]
        }
        binding!!.edtName.setText(profileModel.name)
        if (profileModel.nation == null) {
            profileModel.nation =
                nations.find { it.nationInformation!!.code!!.lowercase().contains("gb") }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.update -> {
                if (!showDialog) {
                    saveChangeProfile()
                }
            }
            R.id.takePicture, R.id.avatar -> {
                pickAvatar()
            }
            R.id.defaultImage -> {
                profileModel.avatarUrl = null
                updateProfile()
            }

            R.id.nationFlag, R.id.txtCountry -> {
                if (!showDialog) {
                    showDialog = true
                    val pos = nations.indexOfFirst {
                        it.nationInformation!!.name.toString()
                            .lowercase() == binding!!.txtCountry.text.toString().lowercase()
                    }
                    nationDialog.setSelectPosition(pos)
                    nationDialog.show(requireActivity().supportFragmentManager, "nation")
                }
            }
            R.id.pickDate, R.id.txtBirthday -> {
                pickBirthdayDialog.onDatePickerListener = this
                pickBirthdayDialog.setCurDate(
                    profileModel.dateOfBirth ?: System.currentTimeMillis()
                )
                if (!pickBirthdayDialog.isAdded) {
                    pickBirthdayDialog.show(
                        requireActivity().supportFragmentManager,
                        "pick_birthday"
                    )
                }
            }
            R.id.txtSelectJob -> {
                openJobDialog()
            }
            R.id.txtHeightUnit -> {
                openHeightUnitDialog()
            }
            R.id.txtWeightUnit -> {
                openWeightUnitDialog()
            }
        }
    }

    private fun pickAvatar() {
        val pickActionDiaLog = PickActionDiaLog.create(
            getString(R.string.profile_avatar_dialog_title),
            ArrayList(actionModelList)
        )
        pickActionDiaLog.onActionPickerListener = this
        pickActionDiaLog.show(requireActivity().supportFragmentManager, "pick_avatar")
    }

    private fun updateProfile() {
        binding!!.profile = profileModel
    }

    private fun saveChangeProfile() {
        profileModel.name = binding!!.edtName.text?.trim().toString()
        profileModel.job = binding!!.edtJob.text?.trim().toString()
        profileModel.height = if (binding!!.edtHeight.text?.trim().isNullOrEmpty()) {
            null
        } else {
            try {
                Utils.roundDecimalNumber(binding!!.edtHeight.text.trim().toString())
            } catch (ex: NumberFormatException) {
                null
            }
        }
        profileModel.weight = if (binding!!.edtWeight.text?.trim().isNullOrEmpty()) {
            null
        } else {
            try {
                Utils.roundDecimalNumber(binding!!.edtWeight.text.trim().toString())
            } catch (ex: NumberFormatException) {
                null
            }
        }
        dataPref.setUserProfileModel(args.isCrush, profileModel)

        SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
        findNavController().navigateUp()

    }

    private fun checkStoragePermission() {
        if (!SystemUtils.hasPermissions(requireContext(), *getStoragePermissions())) {
            storageResultLauncher.launch(getStoragePermissions())
        } else {
            pickPhoto()
        }
    }


    private fun checkCameraPermission() {
        if (!SystemUtils.hasPermissions(requireContext(), *Constants.TAKE_PICTURE_PERMISSION)) {
            cameraResultLauncher.launch(Constants.TAKE_PICTURE_PERMISSION)
        } else {
            takePicture()
        }
    }

    private fun takePicture() {
        val takePicture = TakePicture.onCreate()
        takePicture.setListener(this)
        takePicture.show(requireActivity().supportFragmentManager, "take_picture")
    }

    private val storageResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (SystemUtils.hasPermissions(requireContext(), *getStoragePermissions())) {
                pickPhoto()
            } else {
                SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
            }
        }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (SystemUtils.hasPermissions(requireContext(), *Constants.TAKE_PICTURE_PERMISSION)) {
                takePicture()
            } else {
                SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
            }
        }

    private fun openJobDialog() {
        if (!showDialog) {
            showDialog = true
            val pos = jobList.indexOf(profileModel.job?.trim()?.lowercase()).coerceAtLeast(0)
            jobDialog.setSelectPosition(pos)
            jobDialog.show(requireActivity().supportFragmentManager, "job_dialog")
        }
    }

    private fun openHeightUnitDialog() {
        val heightUnits = resources.getStringArray(R.array.height_unit).toMutableList()
        unitPopupDialog.updateData(heightUnits, object : ItemUnitAdapter.OnUnitItemClick {
            override fun onUnitClick(position: Int) {
                profileModel.heightUnitCm = position == 0
                binding!!.txtHeightUnit.text = heightUnits[position]
                unitPopupDialog.close()
            }
        })
        unitPopupDialog.show(binding!!.root.bottom, binding!!.txtHeightUnit)
    }

    private fun openWeightUnitDialog() {
        val weightUnits = resources.getStringArray(R.array.weight_unit).toMutableList()
        unitPopupDialog.updateData(weightUnits, object : ItemUnitAdapter.OnUnitItemClick {
            override fun onUnitClick(position: Int) {
                profileModel.weightUnitKg = position == 0
                binding!!.txtWeightUnit.text = weightUnits[position]
                unitPopupDialog.close()
            }
        })
        unitPopupDialog.show(binding!!.root.bottom, binding!!.txtHeightUnit)
    }

    override fun onPause() {
        super.onPause()
        pickBirthdayDialog.dialog?.dismiss()
        unitPopupDialog.close()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_profile
    }

    override fun onDatePicked(time: Long) {
        profileModel.dateOfBirth = time
        binding!!.txtBirthday.setTestBirthdayTime(time)
    }

    override fun onItemSelected(itemPos: Int) {
        if (itemPos == 0) {
            checkCameraPermission()
        } else {
            checkStoragePermission()
        }
    }

    override fun onDismiss() {
    }

    override fun onPicked(photoModel: PhotoModel?) {
        if (photoModel != null) {
            val cropPhotoDialog = CropPhotoDialog.create(
                photoModel.uri,
                isFromMediaStore = true,
                isSquare = true
                , isRectangle = false)
            cropPhotoDialog.setCroppedImageSize(Constants.PROFILE_PHOTO_CROP_HEIGHT, Constants.PROFILE_PHOTO_CROP_WIDTH)
            cropPhotoDialog.show(parentFragmentManager, "CropImage")
            cropPhotoDialog.onCropDoneListener = object : CropPhotoDialog.OnCropDoneListener {
                override fun onCropDone(uri: Uri?) {
                    if (uri != null) {
                        profileModel.avatarUrl = uri.toString()
                        updateProfile()
                    }
                }
            }

        }
    }

    override fun setImage(uri: Uri) {
        val cropPhotoDialog = CropPhotoDialog.create(uri, isFromMediaStore = true, isSquare = true, isRectangle = false)
        cropPhotoDialog.onCropDoneListener = object : CropPhotoDialog.OnCropDoneListener {
            override fun onCropDone(uri: Uri?) {
                if (uri != null) {
                    profileModel.avatarUrl = uri.toString()
                    updateProfile()
                }
            }
        }
        cropPhotoDialog.show(parentFragmentManager, "crop_image")
    }
}