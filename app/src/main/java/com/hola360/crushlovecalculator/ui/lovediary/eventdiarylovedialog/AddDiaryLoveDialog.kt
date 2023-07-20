package com.hola360.crushlovecalculator.ui.lovediary.eventdiarylovedialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelDialogFragment
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.databinding.LayoutDiaryLoveDialogBinding
import com.hola360.crushlovecalculator.dialog.MessageAlertDialog
import com.hola360.crushlovecalculator.ui.event.dialog.adapter.PickingPhotoAdapter
import com.hola360.crushlovecalculator.ui.event.dialog.pickphoto.PickPhotoStoryDialog
import com.hola360.crushlovecalculator.ui.lovediary.eventdiarylovedialog.weather.WeatherDialog
import com.hola360.crushlovecalculator.utils.*
import com.hola360.crushlovecalculator.utils.Utils.deepEqualTo

class AddDiaryLoveDialog : BaseViewModelDialogFragment<LayoutDiaryLoveDialogBinding>(),
    View.OnClickListener, PickingPhotoAdapter.OnPickPhotoItemClickListener,
    PickPhotoStoryDialog.OnPickImageDoneListener {


    private var mTitle: String? = null
    private var diaryModel: DiaryModel? = null
    private var columns: Int = Constants.AT_LEAST_COLUMN
    private val pickPhotoAdapter = PickingPhotoAdapter()
    private var imageList = mutableListOf<PhotoModel>()
    private lateinit var mLayoutManager: GridLayoutManager
    private lateinit var viewModel: AddDiaryLoveDialogViewModel
    var listener: OnDoneClickListener? = null


    var isDescriptionNotEmpty = false
    var isDescriptionModified = false
    var isImageListNotEmpty = false
    var isImageListModified = false
    var isTitleNotEmpty = false
    var isTitleModified = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        mTitle = requireArguments().getString(KEY_TITLE)
        if (diaryModel != null) {
            isImageListNotEmpty = Utils.jsonToList(diaryModel!!.images).isNotEmpty()
        }
        columns = requireArguments().getInt(KEY_COLUMNS)
        initLayoutManager()
        pickPhotoAdapter.setClickListener(this@AddDiaryLoveDialog)
        initViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                val description = binding!!.etDescription.text.toString().trim()
                val title = binding!!.etTitle.text.toString().trim()
                onBackPressed(description,title)
            }
            true
        }
    }

    override fun initView() {
        binding!!.apply {
            etTitle.isEnabled =true
            imgWeather.setOnClickListener(this@AddDiaryLoveDialog)
            imgFeeling.setOnClickListener(this@AddDiaryLoveDialog)
            toolbar.apply {
                title = mTitle
                setNavigationOnClickListener {
                    onBackPressed(binding!!.etDescription.text.toString().trim(),binding!!.etTitle.text.toString().trim())
                }
                setSafeMenuClickListener { menuItem ->
                    when (menuItem!!.itemId) {
                        R.id.action_done -> {
                            actionDone(binding!!.etDescription.text.toString().trim(),binding!!.etTitle.text.toString().trim())
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
                    if (diaryModel != null) {
                        isDescriptionModified = (description != diaryModel!!.description)
                    }
                    updateMenu()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
            etTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val title = p0!!.toString().trim()
                    isTitleNotEmpty = title.isNotEmpty()
                    if (diaryModel != null) {
                        isTitleModified = (title != diaryModel!!.title)
                    }
                    updateMenu()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            if (diaryModel != null) {
                etDescription.setText(diaryModel!!.description)
                etTitle.setText(diaryModel!!.title)
                imageList.addAll(Utils.jsonToList(diaryModel!!.images))
                pickPhotoAdapter.updateData(Utils.jsonToList(diaryModel!!.images))
            }
        }
        if(diaryModel != null){
            binding!!.tvDate.setDiaryTime(diaryModel!!.time)
        }else{
            binding!!.tvDate.setDiaryTime(System.currentTimeMillis())
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_diary_love_dialog
    }

    override fun initViewModel() {
        val factory = AddDiaryLoveDialogViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[AddDiaryLoveDialogViewModel::class.java]
        diaryModel = requireArguments().getParcelable(KEY_DIARY)
        viewModel.isSaved.observe(this) {
            it?.let {
                if (it) {
                    dismiss()
                    listener?.onSavedDone()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.imgWeather -> {
                val dialog = WeatherDialog.create()
                dialog.show(parentFragmentManager, "dialogIcon")
            }
            R.id.imgFeeling -> {
                val dialog = WeatherDialog.create()
                dialog.show(parentFragmentManager, "dialogIcon")
            }
        }
    }

    private fun initLayoutManager() {
        mLayoutManager = GridLayoutManager(mainActivity, columns)
    }

    interface OnDoneClickListener {
        fun onSavedDone()
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
            diaryModel != null,
        )
        if (!pickPhotoStoryDialog.isAdded) {
            pickPhotoStoryDialog.listener = this@AddDiaryLoveDialog
            pickPhotoStoryDialog.show(mainActivity.supportFragmentManager, "pickStoryPhoto")
        }
    }

    override fun onDeleteItemClick(photoModel: PhotoModel) {
        imageList.remove(photoModel)
        isImageListNotEmpty = imageList.isNotEmpty()
        if (diaryModel != null) {
            isImageListModified = !imageList.deepEqualTo(Utils.jsonToList(diaryModel!!.images))
        }

        updateMenu()
    }

    fun updateMenu() {
        binding!!.toolbar.menu.clear()
        binding!!.toolbar.inflateMenu(
            if (diaryModel == null) {
                if (isDescriptionNotEmpty || isImageListNotEmpty || isTitleNotEmpty) {
                    R.menu.menu_story
                } else {
                    R.menu.menu_story_disable
                }
            } else {
                if (isDescriptionModified || isImageListModified || isTitleModified) {
                    if (!isImageListNotEmpty && !isDescriptionNotEmpty && !isTitleNotEmpty) {
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

    fun actionDone(description: String,title: String) {
        when (checkInformation(description,title)) {
            VALID_INFORMATION -> {
                SystemUtils.hideSoftKeyboard(mainActivity, binding!!.root)
                saveDiary(description,title)
            }
            EMPTY_INFORMATION -> {
                ToastUtils.getInstance(mainActivity).showToast("Empty Information")
            }
            NO_CHANGE_INFORMATION -> {
                dismiss()
            }
        }
    }

    override fun onDoneClickListener(imageList: MutableList<PhotoModel>) {
        pickPhotoAdapter.updateData(imageList)
        this.imageList.apply {
            clear()
            addAll(imageList)
        }
        isImageListNotEmpty = imageList.isNotEmpty()
        if (diaryModel != null) {
            isImageListModified = !imageList.deepEqualTo(Utils.jsonToList(diaryModel!!.images))
        }

        updateMenu()
    }

    fun onBackPressed(description: String,title: String) {
        when (checkInformation(description,title)) {
            VALID_INFORMATION -> {
                showMessageDialogUpdateStory(description,title)
            }
            else -> {
                dismiss()
            }
        }
    }

    fun showMessageDialogUpdateStory(description: String,title: String) {
        val message = resources.getString(R.string.string_diary_save)
        val titleDialog = resources.getString(R.string.string_story_title_save)
        val messageAlertDialog = MessageAlertDialog.create(titleDialog, message, "Discard", "Save")
        messageAlertDialog.onAlertMessageDialogClickListener =
            object : MessageAlertDialog.OnAlertMessageDialogClickListener {
                override fun onPositive() {
                    saveDiary(description,title)
                }

                override fun onNegative() {
                    messageAlertDialog.dismiss()
                    this@AddDiaryLoveDialog.dismiss()
                }
            }

        if (!messageAlertDialog.isAdded) {
            messageAlertDialog.show(parentFragmentManager, "SaveStory")
        }
    }

    fun saveDiary(description: String,title: String) {
        if (diaryModel != null) {
            viewModel.updateDiary(
                diaryModel!!,
                description,
                imageList,title
            )
        } else {
            viewModel.addDiary(description, imageList,title)
        }
    }

    fun checkInformation(description: String , title: String): Int {
        if (description.isEmpty() && imageList.isEmpty() && title.isEmpty()) {
            return EMPTY_INFORMATION
        }
        if (diaryModel != null) {
            val oldList = Utils.jsonToList(diaryModel!!.images)
            val oldDescription = diaryModel!!.description
            val oldTitle = diaryModel!!.title
            if (oldDescription == description && oldList.deepEqualTo(imageList) && oldTitle == title) {
                return NO_CHANGE_INFORMATION
            }
        }
        return VALID_INFORMATION
    }


    companion object {

        private const val KEY_TITLE = "Key_Title"
        private const val KEY_COLUMNS = "Key_Column"
        private const val KEY_DIARY = "Key_Diary"
        const val VALID_INFORMATION = 1
        const val EMPTY_INFORMATION = 2
        const val NO_CHANGE_INFORMATION = 3

        fun create(title: String, column: Int): AddDiaryLoveDialog {
            val dialog = AddDiaryLoveDialog()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putInt(KEY_COLUMNS, column)
            dialog.arguments = bundle
            return dialog
        }

        fun createEdit(title: String,column: Int,diaryModel: DiaryModel): AddDiaryLoveDialog {
            val dialog = AddDiaryLoveDialog()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putInt(KEY_COLUMNS, column)
            bundle.putParcelable(KEY_DIARY,diaryModel)
            dialog.arguments = bundle
            return dialog
        }
    }
}