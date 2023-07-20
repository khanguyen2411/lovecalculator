package com.hola360.crushlovecalculator.ui.event.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseFullAlertDialog
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.data.model.story.StoryModel
import com.hola360.crushlovecalculator.databinding.LayoutStoryDialogBinding
import com.hola360.crushlovecalculator.dialog.MessageAlertDialog
import com.hola360.crushlovecalculator.ui.event.dialog.adapter.PickingPhotoAdapter
import com.hola360.crushlovecalculator.ui.event.dialog.pickphoto.PickPhotoStoryDialog
import com.hola360.crushlovecalculator.utils.*
import com.hola360.crushlovecalculator.utils.Utils.deepEqualTo

class StoryDialog : BaseFullAlertDialog<LayoutStoryDialogBinding>(),
    PickingPhotoAdapter.OnPickPhotoItemClickListener, PickPhotoStoryDialog.OnPickImageDoneListener {

    private var mTitle: String? = null
    private var eventId: Int? = null
    private var storyModel: StoryModel? = null
    private var columns: Int = Constants.AT_LEAST_COLUMN
    private val pickPhotoAdapter = PickingPhotoAdapter()
    private var imageList = mutableListOf<PhotoModel>()
    private lateinit var mLayoutManager: GridLayoutManager
    private lateinit var viewModel: StoryDialogViewModel
    var listener: OnDoneClickListener? = null

    var isDescriptionNotEmpty = false
    var isImageListNotEmpty = false
    var isDescriptionModified = false
    var isImageListModified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        mTitle = requireArguments().getString(KEY_TITLE)
        storyModel = requireArguments().getParcelable(KEY_STORY_MODEL)
        if (storyModel != null) {
            isImageListNotEmpty = Utils.jsonToList(storyModel!!.images).isNotEmpty()
        }
        eventId = requireArguments().getInt(KEY_EVENT_ID)
        columns = requireArguments().getInt(KEY_COLUMNS)
        initLayoutManager()
        pickPhotoAdapter.setClickListener(this@StoryDialog)
        initViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                val description = binding!!.etDescription.text.toString().trim()
                onBackPressed(description)
            }
            true
        }
    }

    private fun initViewModel() {
        val factory = StoryDialogViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[StoryDialogViewModel::class.java]

        viewModel.isSaved.observe(this) {
            it?.let {
                if (it) {
                    dismiss()
                    listener?.onSavedDone()
                }
            }
        }
    }

    override fun initView(
    ) {
        binding!!.apply {
            mViewModel = viewModel
            toolbar.apply {
                title = mTitle
                setNavigationOnClickListener {
                    onBackPressed(binding!!.etDescription.text.toString().trim())
                }
                setSafeMenuClickListener { menuItem ->
                    when (menuItem!!.itemId) {
                        R.id.action_done -> {
                            actionDone(binding!!.etDescription.text.toString().trim())
                        }
                    }
                }
            }
            rvPickPhoto.apply {
                adapter = pickPhotoAdapter
                layoutManager = mLayoutManager
            }

            etDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val description = p0!!.toString().trim()
                    isDescriptionNotEmpty = description.isNotEmpty()
                    if (storyModel != null) {
                        isDescriptionModified = (description != storyModel!!.description)
                    }
                    updateMenu()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            if (storyModel != null) {
                etDescription.setText(storyModel!!.description)
                imageList.addAll(Utils.jsonToList(storyModel!!.images))
                pickPhotoAdapter.updateData(Utils.jsonToList(storyModel!!.images))
            }
        }
    }

    fun showMessageDialogUpdateStory(description: String) {
        val message = resources.getString(R.string.string_story_save)
        val title = resources.getString(R.string.string_story_title_save)
        val messageAlertDialog = MessageAlertDialog.create(title, message, "Discard", "Save")
        messageAlertDialog.onAlertMessageDialogClickListener =
            object : MessageAlertDialog.OnAlertMessageDialogClickListener {
                override fun onPositive() {
                    saveStory(description)
                }

                override fun onNegative() {
                    messageAlertDialog.dismiss()
                    this@StoryDialog.dismiss()
                }
            }

        if (!messageAlertDialog.isAdded) {
            messageAlertDialog.show(parentFragmentManager, "SaveStory")
        }
    }

    fun saveStory(description: String) {
        if (storyModel != null) {
            viewModel.updateStory(
                storyModel!!,
                description,
                imageList,
                eventId!!
            )
        } else {
            viewModel.addStory(description, imageList, eventId!!)
        }
    }

    fun actionDone(description: String) {
        when (checkInformation(description)) {
            VALID_INFORMATION -> {
                SystemUtils.hideSoftKeyboard(mainActivity, binding!!.root)
                saveStory(description)
            }
            EMPTY_INFORMATION -> {
                ToastUtils.getInstance(mainActivity).showToast("Empty Information")
            }
            NO_CHANGE_INFORMATION -> {
                dismiss()
            }
        }
    }

    fun checkInformation(description: String): Int {
        if (description.isEmpty() && imageList.isEmpty()) {
            return EMPTY_INFORMATION
        }
        if (storyModel != null) {
            val oldList = Utils.jsonToList(storyModel!!.images)
            val oldDescription = storyModel!!.description
            if (oldDescription == description && oldList.deepEqualTo(imageList)) {
                return NO_CHANGE_INFORMATION
            }
        }
        return VALID_INFORMATION
    }

    fun onBackPressed(description: String) {
        when (checkInformation(description)) {
            VALID_INFORMATION -> {
                showMessageDialogUpdateStory(description)
            }
            else -> {
                dismiss()
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_story_dialog
    }

    private fun initLayoutManager() {
        mLayoutManager = GridLayoutManager(mainActivity, columns)
    }

    companion object {
        private const val KEY_STORY_MODEL = "Key_Story_Model"
        private const val KEY_TITLE = "Key_Title"
        private const val KEY_EVENT_ID = "Key_EventID"
        private const val KEY_COLUMNS = "Key_Column"

        fun create(title: String, eventId: Int, column: Int): StoryDialog {
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putInt(KEY_EVENT_ID, eventId)
            bundle.putInt(KEY_COLUMNS, column)
            val dialog = StoryDialog()
            dialog.arguments = bundle
            return dialog
        }

        fun create(title: String, eventId: Int, column: Int, storyModel: StoryModel): StoryDialog {
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putParcelable(KEY_STORY_MODEL, storyModel)
            bundle.putInt(KEY_EVENT_ID, eventId)
            bundle.putInt(KEY_COLUMNS, column)
            val dialog = StoryDialog()
            dialog.arguments = bundle
            return dialog
        }

        const val VALID_INFORMATION = 1
        const val EMPTY_INFORMATION = 2
        const val NO_CHANGE_INFORMATION = 3
    }

    override fun onAddItemClick() {
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

    fun updateMenu() {
        binding!!.toolbar.menu.clear()
        binding!!.toolbar.inflateMenu(
            if (storyModel == null) {
                if (isDescriptionNotEmpty || isImageListNotEmpty) {
                    R.menu.menu_story
                } else {
                    R.menu.menu_story_disable
                }
            } else {
                if (isDescriptionModified || isImageListModified) {
                    if (!isImageListNotEmpty && !isDescriptionNotEmpty) {
                        R.menu.menu_story_disable
                    } else {
                        R.menu.menu_story
                    }
                } else {
                    R.menu.menu_story_disable
                }
            }
        )
    }

    override fun onDeleteItemClick(photoModel: PhotoModel) {
        imageList.remove(photoModel)
        isImageListNotEmpty = imageList.isNotEmpty()
        if (storyModel != null) {
            isImageListModified = !imageList.deepEqualTo(Utils.jsonToList(storyModel!!.images))
        }

        updateMenu()
    }

    private val storageResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            onPermissionResult()
        }

    fun onPermissionResult() {
        if (SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())) {
            pickPhoto()
        } else {
            SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
        }
    }

    fun pickPhoto() {
        val pickPhotoStoryDialog = PickPhotoStoryDialog.create(
            imageList,
            storyModel != null,
        )
        if (!pickPhotoStoryDialog.isAdded) {
            pickPhotoStoryDialog.listener = this@StoryDialog
            pickPhotoStoryDialog.show(mainActivity.supportFragmentManager, "pickStoryPhoto")
        }
    }

    override fun onDoneClickListener(imageList: MutableList<PhotoModel>) {
        pickPhotoAdapter.updateData(imageList)
        this.imageList.apply {
            clear()
            addAll(imageList)
        }
        isImageListNotEmpty = imageList.isNotEmpty()
        if (storyModel != null) {
            isImageListModified = !imageList.deepEqualTo(Utils.jsonToList(storyModel!!.images))
        }

        updateMenu()
    }

    interface OnDoneClickListener {
        fun onSavedDone()
    }
}