package com.hola360.crushlovecalculator.ui.event.eventdetail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.data.model.story.StoryModel
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.databinding.FragmentEventDetailBinding
import com.hola360.crushlovecalculator.dialog.MessageAlertDialog
import com.hola360.crushlovecalculator.dialog.action.PickActionDiaLog
import com.hola360.crushlovecalculator.dialog.action.model.ActionModel
import com.hola360.crushlovecalculator.dialog.crop.CropPhotoDialog
import com.hola360.crushlovecalculator.dialog.photopicker.PickPhotoDialog
import com.hola360.crushlovecalculator.ui.event.adapter.ActionAdapter
import com.hola360.crushlovecalculator.ui.event.dialog.StoryDialog
import com.hola360.crushlovecalculator.ui.event.popup.EventActionModel
import com.hola360.crushlovecalculator.ui.event.popup.ListActionPopup
import com.hola360.crushlovecalculator.ui.event.store.CoverStoreDialogFragment
import com.hola360.crushlovecalculator.ui.event.store.detailcoverstore.DetailCoverStoreDiaLog
import com.hola360.crushlovecalculator.ui.event.storydetail.ImageDetailDialog
import com.hola360.crushlovecalculator.ui.profile.TakePicture
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.clickWithDebounce
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType

class EventDetailFragment : BaseViewModelFragment<FragmentEventDetailBinding>(),
    EventDetailAdapter.OnItemClickListener,
    PickActionDiaLog.OnActionPickerListener, TakePicture.SetImageToCamera,
    PickPhotoDialog.OnPhotoPickListener, CoverStoreDialogFragment.OnClickItem,
    DetailCoverStoreDiaLog.OnLickItemDetaiImageCover, StoryDialog.OnDoneClickListener,
    StoryImageAdapter.OnPhotoStoryClickListener {

    private lateinit var viewModel: EventDetailViewModel
    private lateinit var mLayoutManager: GridLayoutManager
    private lateinit var app: App
    private lateinit var mAdapter: EventDetailAdapter

    private val args: EventDetailFragmentArgs by lazy {
        EventDetailFragmentArgs.fromBundle(requireArguments())
    }

    private val actionPopup by lazy {
        ListActionPopup(mainActivity)
    }

    val actions = mutableListOf(
        EventActionModel(R.drawable.ic_action_edit, "Edit Story"),
        EventActionModel(R.drawable.ic_action_delete, "Delete Story")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = requireActivity().application as App
        mAdapter = EventDetailAdapter(mainActivity.application)
        mAdapter.apply {
            setMaxColumns(app.storyImageColumns)
            setListener(this@EventDetailFragment)
            imageStoryListener = this@EventDetailFragment
        }
    }

    override fun initView() {
        binding!!.btnCamera.clickWithDebounce{clickBg()}
        binding!!.btnBackground.clickWithDebounce { clickBg() }
        initLayoutManager()
        binding!!.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            eventModel = args.eventModel
            mViewModel = viewModel
            rvStory.apply {
                adapter = mAdapter
                layoutManager =
                    LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
                mAdapter.setLayoutManager(mLayoutManager)
            }
            fabAddStory.clickWithDebounce {
                val addStoryDialog = StoryDialog.create(
                    "Add New Story",
                    args.eventModel?.eventId!!,
                    app.storyImageColumns
                )
                addStoryDialog.listener = this@EventDetailFragment
                if (!addStoryDialog.isAdded) {
                    addStoryDialog.show(mainActivity.supportFragmentManager, "addNewStory")
                }
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_event_detail
    }

    override fun initViewModel() {
        val factory =
            EventDetailViewModel.Factory(mainActivity.application, args.eventModel!!.eventId!!)
        viewModel = ViewModelProvider(this, factory)[EventDetailViewModel::class.java]
        initObserveData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchData()
    }

    fun initObserveData() {
        viewModel.uiData.observe(this) {
            it?.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        val storyList = (it as DataResponse.DataSuccessResponse).body
                        mAdapter.updateData(storyList)
                    }
                    LoadDataStatus.ERROR -> {
                        mAdapter.clearData()
                        binding!!.appBarLayout.setExpanded(true)
                    }
                    else -> {}
                }
            }
        }
        viewModel.isUpdateDone.observe(this) {
            it?.let {
                if (it) {
                    binding!!.eventModel = args.eventModel
                }
            }
        }

        viewModel.isDeleteDone.observe(this) {
            it?.let {
                if (it) {
                    viewModel.fetchData()
                }
            }
        }
    }

    private fun initLayoutManager() {
        mLayoutManager = GridLayoutManager(requireContext(), app.storyImageColumns)
    }

    override fun onMoreIconClick(view: View, storyModel: StoryModel) {
        actionPopup.setup(view, actions, object : ActionAdapter.OnActionListener {
            override fun onItemClickListener(position: Int) {
                when (position) {
                    0 -> {
                        val editStoryDialog = StoryDialog.create(
                            "Edit Story",
                            args.eventModel?.eventId!!,
                            app.storyImageColumns,
                            storyModel
                        )
                        editStoryDialog.listener = this@EventDetailFragment
                        if (!editStoryDialog.isAdded) {
                            editStoryDialog.show(
                                mainActivity.supportFragmentManager,
                                "editStory"
                            )
                        }
                    }
                    1 -> {
                        showMessageDialogDeleteStory(storyModel)
                    }
                }
            }
        })
    }

    override fun onRootItemClick(storyModel: StoryModel) {
        findNavController().navigate(
            NavMainGraphDirections.actionGlobalStoryDetailFragment(
                storyModel, args.eventModel
            )
        )
    }

    fun showMessageDialogDeleteStory(storyModel: StoryModel) {
        val message = getString(R.string.string_story_delete)
        val title = getString(R.string.string_story_title_delete)
        val messageAlertDialog = MessageAlertDialog.create(title, message)
        messageAlertDialog.onAlertMessageDialogClickListener =
            object : MessageAlertDialog.OnAlertMessageDialogClickListener {
                override fun onPositive() {
                    viewModel.deleteStory(storyModel = storyModel)
                }

                override fun onNegative() {
                    messageAlertDialog.dismiss()
                }
            }
        if (!messageAlertDialog.isAdded) {
            messageAlertDialog.show(parentFragmentManager, "DeleteStory")
        }
    }

    fun clickBg() {
        pickActionDialog.onActionPickerListener = this
        if (!pickActionDialog.isAdded) {
            pickActionDialog.show(requireActivity().supportFragmentManager, "changeBanner")
        }
    }

    private val pickPhotoDialog by lazy {
        PickPhotoDialog.create()
    }

    private val pickActionDialog by lazy {
        PickActionDiaLog.create(
            getString(R.string.string_story_change_banner),
            ArrayList(actionModelList)
        )
    }

    private val actionModelList: List<ActionModel> by lazy {
        val actions = actionIcons.flatMapIndexed { index, item ->
            val actionsList = arrayListOf(ActionModel(item, actionTitle[index], false))
            actionsList
        }
        actions
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
                val dialog = CoverStoreDialogFragment.create()
                dialog.onclickListerner = this
                dialog.show(parentFragmentManager, "coveStoreDialog")
            }
        }
    }

    private fun pickPhoto() {
        pickPhotoDialog.onPhotoPickListener = this
        pickPhotoDialog.show(parentFragmentManager, "PickPhoto")
    }

    private val storageResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            onPermissionResult()
        }

    private fun onPermissionResult() {
        if (SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())) {
            pickPhoto()
        } else {
            SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
        }
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
        if(!takePicture.isAdded){
            takePicture.show(requireActivity().supportFragmentManager, "take_picture")
        }

    }

    override fun onDismiss() {
    }

    override fun setImage(uri: Uri) {
        val cropPhotoDialog = CropPhotoDialog.create(
            uri,
            isFromMediaStore = true,
            isSquare = false,
            isRectangle = true
        )
        cropPhotoDialog.onCropDoneListener = object : CropPhotoDialog.OnCropDoneListener {
            override fun onCropDone(uri: Uri?) {
                if (uri != null) {
                    args.eventModel?.thumb = uri.toString()
                    viewModel.updateBgEvent(args.eventModel?.eventId!!, uri.toString())
                }
            }
        }
        cropPhotoDialog.show(parentFragmentManager, "crop_image")
    }

    override fun onPicked(photoModel: PhotoModel?) {
        if (photoModel != null) {
            val cropPhotoDialog = CropPhotoDialog.create(
                photoModel.uri,
                isFromMediaStore = true,
                isSquare = false,
                isRectangle = true
            )
            cropPhotoDialog.setCroppedImageSize(
                Constants.BACKGROUND_CROP_HEIGHT,
                Constants.BACKGROUND_CROP_WIDTH
            )
            cropPhotoDialog.onCropDoneListener = object : CropPhotoDialog.OnCropDoneListener {
                override fun onCropDone(uri: Uri?) {
                    if (uri != null) {
                        args.eventModel?.thumb = uri.toString()
                        viewModel.updateBgEvent(args.eventModel?.eventId!!, uri.toString())
                    }
                }
            }
            cropPhotoDialog.show(parentFragmentManager, "CropImage")
        }
    }

    override fun onClickItemImage(full: String) {
        args.eventModel?.thumb = full
        viewModel.updateBgEvent(args.eventModel?.eventId!!, full)
    }

    override fun onLickItemImageCover(full: String) {
        args.eventModel?.thumb = full
        viewModel.updateBgEvent(args.eventModel?.eventId!!, full)
    }

    override fun onSavedDone() {
        mainActivity.showSnackBar(
            SnackBarType.Success,
            resources.getString(R.string.title_success),
            "Post Successfully"
        )
        viewModel.fetchData()
    }

    override fun onPhotoStoryClickListener(position: Int, imageList: MutableList<PhotoModel>) {
        val dialog = ImageDetailDialog.create(position, imageList)
        if (!dialog.isAdded) {
            dialog.show(parentFragmentManager, "StoryDetail")
        }
    }
}