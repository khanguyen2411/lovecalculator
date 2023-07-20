package com.hola360.crushlovecalculator.ui.lovetime

import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.databinding.FragmentLoveTimeBinding
import com.hola360.crushlovecalculator.dialog.action.PickActionDiaLog
import com.hola360.crushlovecalculator.dialog.action.model.ActionModel
import com.hola360.crushlovecalculator.dialog.crop.CropPhotoDialog
import com.hola360.crushlovecalculator.dialog.lovetimedialog.DatePickerDiaLog
import com.hola360.crushlovecalculator.dialog.photopicker.PickPhotoDialog
import com.hola360.crushlovecalculator.ui.lovetime.base.BaseLoveTimeFragment
import com.hola360.crushlovecalculator.ui.lovetime.emoijconnect.EmojiDialogFragment
import com.hola360.crushlovecalculator.ui.lovetime.shareLoveTimeDialog.ShareLoveTimeDialog
import com.hola360.crushlovecalculator.ui.profile.TakePicture
import com.hola360.crushlovecalculator.utils.*
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType

class LoveTimeFragment : BaseLoveTimeFragment<FragmentLoveTimeBinding>(), View.OnClickListener,
    DatePickerDiaLog.OnDatePickerListener, PickActionDiaLog.OnActionPickerListener,
    PickPhotoDialog.OnPhotoPickListener, EmojiDialogFragment.OnSetEmojiSuccessListener,
    TakePicture.SetImageToCamera {
    private var viewModel: LoveTimeViewModel? = null
    private val emojiDialog by lazy {
        EmojiDialogFragment.create()
    }
    private val pickPhotoDialog by lazy {
        PickPhotoDialog.create()
    }
    private val pickActionDialog by lazy {
        PickActionDiaLog.create(
            getString(R.string.change_background),
            ArrayList(actionModelList)
        )
    }
    private val actionIcons: ArrayList<Int> by lazy {
        arrayListOf(
            R.drawable.ic_lovetime_dialog_camera,
            R.drawable.ic_lovetime_dialog_gallery,
            R.drawable.ic_lovetime_dialog_store
        )
    }
    private val actionTitle: Array<String> by lazy {
        resources.getStringArray(R.array.change_background_action)
    }
    private val actionModelList: List<ActionModel> by lazy {
        val actions = actionIcons.flatMapIndexed { index, item ->
            val actionsList = arrayListOf(ActionModel(item, actionTitle[index], false))
            actionsList
        }
        actions
    }

    private val datePickerDiaLog: DatePickerDiaLog by lazy {
        DatePickerDiaLog.create(getString(R.string.start_falling_in_love), false)
    }

    private fun changeTheme() {
        findNavController().navigate(LoveTimeFragmentDirections.actionGlobalThemeStoreFragment())
    }

    private fun changeBackground() {
        pickActionDialog.onActionPickerListener = this
        pickActionDialog.show(requireActivity().supportFragmentManager, "changeBg")
    }


    override fun initView() {
        setNavigationIcon()
        setToolbarMenu()
        binding!!.toolbar.setNavigationOnClickListener { onBackPress() }
        binding!!.toolbar.setSafeMenuClickListener { menuItem ->
            if (!pickActionDialog.isVisible) {
                when (menuItem!!.itemId) {
                    R.id.action_add_event -> {
                        findNavController().navigate(R.id.eventFragment)
                    }
                    R.id.action_share -> {
                        if (dataPref.getStartLovingDate() != 0L) {
                            val shareDiaLog = ShareLoveTimeDialog.create()
                            shareDiaLog.show(
                                requireActivity().supportFragmentManager,
                                "share_dialog"
                            )
                        } else {
                            showSuccessSnackBar()
                        }
                    }
                    R.id.action_change_background -> {
                        changeBackground()
                    }
                    R.id.action_change_theme -> {
                        changeTheme()
                    }
                    R.id.action_change_emoji -> {
                        gotoEmojiConnection()
                    }
                }
            }
        }
        binding!!.imCrushAvatar.setOnClickListener(this)
        binding!!.imOwnerAvatar.setOnClickListener(this)
        binding!!.myTextViewName.setOnClickListener(this)
        binding!!.myTextViewCrushName.setOnClickListener(this)
        binding!!.myLayoutPickDate.clickWithDebounce { layoutPickDateClick() }
        binding!!.imEmojiConnection.clickWithDebounce { gotoEmojiConnection() }
        binding!!.viewModel = viewModel
        viewModel!!.fetchLoveTime(false)
    }

    private fun onBackPress() {
        findNavController().navigateUp()
    }

    private fun layoutPickDateClick() {
        datePickerDiaLog.onDatePickerListener = this
        datePickerDiaLog.setCurDate(
            if (dataPref.getStartLovingDate() != 0L) {
                dataPref.getStartLovingDate()
            } else {
                System.currentTimeMillis()
            }
        )
        if (!emojiDialog.isAdded) {
            datePickerDiaLog.show(
                parentFragmentManager,
                "pick_datetime"
            )
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imOwnerAvatar, R.id.myTextViewName -> {
                findNavController().navigate(NavMainGraphDirections.actionToNavProfile(false))
            }
            R.id.imCrushAvatar, R.id.myTextViewCrushName -> {
                findNavController().navigate(NavMainGraphDirections.actionToNavProfile(true))
            }
        }
    }

    private fun gotoEmojiConnection() {
        emojiDialog.onSetEmojiSuccessListener = this
        emojiDialog.show(requireActivity().supportFragmentManager, "emojiDlg")
    }

    override fun getLayout(): Int {
        return R.layout.fragment_love_time
    }

    override fun initViewModel() {
        val factory = LoveTimeViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[LoveTimeViewModel::class.java]
    }

    override fun onDatePicked(time: Long) {
        dataPref.setStartLovingDate(time)
        viewModel!!.fetchLoveTime(false)
    }

    override fun onItemSelected(itemPos: Int) {
        when (itemPos) {
            0 -> {
                if (!SystemUtils.hasPermissions(
                        requireContext(),
                        *Constants.TAKE_PICTURE_PERMISSION
                    )
                ) {
                    cameraResultLauncher.launch(Constants.TAKE_PICTURE_PERMISSION)
                } else {
                    cropBgToCamera()
                }
            }
            1 -> {
                if (!SystemUtils.hasPermissions(
                        requireContext(),
                        *SystemUtils.getStoragePermissions()
                    )
                ) {
                    storageResultLauncher.launch(SystemUtils.getStoragePermissions())
                } else {
                    pickPhoto()
                }
            }
            else -> {
                gotoImageStore()
            }
        }
    }

    private fun gotoImageStore() {
        findNavController().navigate(LoveTimeFragmentDirections.actionGlobalImageStoreFragment())
    }

    override fun onDismiss() {
    }

    private fun pickPhoto() {
        pickPhotoDialog.onPhotoPickListener = this
        pickPhotoDialog.show(parentFragmentManager, "PickPhoto")
    }

    override fun onPicked(photoModel: PhotoModel?) {
        if (photoModel != null) {
            val cropPhotoDialog = CropPhotoDialog.create(
                photoModel.uri,
                isFromMediaStore = true,
                isSquare = false
                , isRectangle = false)
            cropPhotoDialog.setCroppedImageSize(Constants.BACKGROUND_CROP_HEIGHT, Constants.BACKGROUND_CROP_WIDTH)
            cropPhotoDialog.onCropDoneListener = object : CropPhotoDialog.OnCropDoneListener {
                override fun onCropDone(uri: Uri?) {
                    if (uri != null) {
                        SharedPreferenceUtils.getInstance(mainActivity)
                            .setLoveBg(uri.toString())
                        viewModel!!.fetchLoveTime(false)
                    }
                }
            }
            cropPhotoDialog.show(parentFragmentManager, "CropImage")
        }
    }

    override fun onPermissionResult() {
        if (SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())) {
            pickPhoto()
        } else {
            SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
        }
    }

    private fun showSuccessSnackBar() {
        mainActivity.showSnackBar(
            SnackBarType.Warning,
            getString(R.string.title_warning),
            getString(R.string.love_time_enter_start_love)
        )
    }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (SystemUtils.hasPermissions(requireContext(), *Constants.TAKE_PICTURE_PERMISSION)) {
                cropBgToCamera()
            } else {
                SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
            }
        }

    private fun cropBgToCamera() {
        val takePicture = TakePicture.onCreate()
        takePicture.setListener(this)
        takePicture.show(requireActivity().supportFragmentManager, "take_picture")
    }

    override fun onSetEmojiSuccess() {
        mainActivity.showSnackBar(
            SnackBarType.Success, getString(R.string.title_success),
            getString(R.string.msg_change_emoji)
        )
        viewModel!!.fetchLoveTime(false)
    }

    override fun setImage(uri: Uri) {
        val cropPhotoDialog = CropPhotoDialog.create(uri, isFromMediaStore = true, isSquare = false, isRectangle = false)
        cropPhotoDialog.onCropDoneListener = object : CropPhotoDialog.OnCropDoneListener {
            override fun onCropDone(uri: Uri?) {
                if (uri != null) {
                    SharedPreferenceUtils.getInstance(mainActivity)
                        .setLoveBg(uri.toString())
                    viewModel!!.fetchLoveTime(false)
                }
            }
        }
        cropPhotoDialog.show(parentFragmentManager, "crop_image")
    }

    override fun setNavigationIcon() {
        binding!!.toolbar.setNavigationIcon(R.drawable.ic_toolbar_back)
    }

    override fun setToolbarMenu() {
        binding!!.toolbar.inflateMenu(R.menu.menu_love_time)
    }
}