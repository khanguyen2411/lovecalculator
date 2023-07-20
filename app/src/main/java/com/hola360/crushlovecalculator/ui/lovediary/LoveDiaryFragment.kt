package com.hola360.crushlovecalculator.ui.lovediary

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.databinding.FragmentLoveDiaryBinding
import com.hola360.crushlovecalculator.dialog.MessageAlertDialog
import com.hola360.crushlovecalculator.ui.event.adapter.ActionAdapter
import com.hola360.crushlovecalculator.ui.event.popup.EventActionModel
import com.hola360.crushlovecalculator.ui.event.popup.ListActionPopup
import com.hola360.crushlovecalculator.ui.lovediary.eventdiarylovedialog.AddDiaryLoveDialog
import com.hola360.crushlovecalculator.utils.setSafeMenuClickListener
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType

class LoveDiaryFragment : BaseViewModelFragment<FragmentLoveDiaryBinding>(), View.OnClickListener,
    AddDiaryLoveDialog.OnDoneClickListener,
    LoveDiaryAdapter.OnItemClickListener {
    private lateinit var viewModel: LoveDiaryFragmentViewModel
    private lateinit var mLayoutManager: GridLayoutManager
    private lateinit var app: App
    private var mAdapter: LoveDiaryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = requireActivity().application as App
        mAdapter = LoveDiaryAdapter(mainActivity.application)
        mAdapter!!.apply {
            setMaxColumns(app.storyImageColumns)
            setListener(this@LoveDiaryFragment)
//            imageStoryListener = this@LoveDiaryFragment
        }
    }

    private val actionPopup by lazy {
        ListActionPopup(mainActivity)
    }

    val actions = mutableListOf(
        EventActionModel(R.drawable.ic_action_edit, "Edit Diary"),
        EventActionModel(R.drawable.ic_action_delete, "Delete Diary")
    )

    override fun initView() {
        initLayoutManager()
        binding!!.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            addDiaryLove.setOnClickListener(this@LoveDiaryFragment)
            rcvDiary.apply {
                adapter = mAdapter
                layoutManager =
                    LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
                mAdapter!!.setLayoutManager(mLayoutManager)
            }
            toolbar.setSafeMenuClickListener {
                when (it!!.itemId) {
                    R.id.action_calendar -> {
                        findNavController().navigate(R.id.action_global_calendarFragment)
                    }

                    R.id.action_search -> {
                        findNavController().navigate(R.id.action_global_searchDiaryFragment)
                    }
                }
            }
        }
        viewModel.fetchData()
    }

    private fun initLayoutManager() {
        mLayoutManager = GridLayoutManager(requireContext(), app.storyImageColumns)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_love_diary
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.add_diary_love -> {
                val dialog = AddDiaryLoveDialog.create("Add Diary love", app.storyImageColumns)
                dialog.listener = this@LoveDiaryFragment
                dialog.show(parentFragmentManager, "diaryDialog")
            }
        }
    }

    override fun initViewModel() {
        val factory = LoveDiaryFragmentViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[LoveDiaryFragmentViewModel::class.java]
        initObserveData()
        viewModel.fetchData()
    }

    fun initObserveData() {
        viewModel.uiData.observe(this) {
            it?.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        val diaryList = (it as DataResponse.DataSuccessResponse).body
                        mAdapter!!.updateData(diaryList)
                        binding!!.progessbar.visibility = View.GONE
                        binding!!.layoutEmpty.visibility = View.GONE
                    }
                    LoadDataStatus.ERROR -> {
                        mAdapter!!.clearData()
                        binding!!.layoutEmpty.visibility = View.VISIBLE
                        binding!!.progessbar.visibility = View.GONE
                    }
                    else -> {
                        binding!!.progessbar.visibility = View.VISIBLE
                        binding!!.layoutEmpty.visibility = View.GONE
                    }
                }
            }
        }
        viewModel.isUpdateDone.observe(this) {
            it?.let {
                if (it) {
                    viewModel.fetchData()
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
        viewModel.fetchData()
    }

    override fun onMoreIconClick(view: View, diaryModel: DiaryModel) {
        actionPopup.setup(view, actions, object : ActionAdapter.OnActionListener {
            override fun onItemClickListener(position: Int) {
                when (position) {
                    0 -> {
                        val dialog = AddDiaryLoveDialog.createEdit(
                            "Edit Diary love",
                            app.storyImageColumns,
                            diaryModel
                        )
                        dialog.listener = this@LoveDiaryFragment
                        dialog.show(parentFragmentManager, "diaryDialog")
                    }
                    1 -> {
                        showMessageDialogDeleteDiary(diaryModel)
                    }
                }
            }

        })
    }

    fun showMessageDialogDeleteDiary(diaryModel: DiaryModel) {
        val message = getString(R.string.string_diary_delete)
        val title = getString(R.string.string_story_title_delete)
        val messageAlertDialog = MessageAlertDialog.create(title, message)
        messageAlertDialog.onAlertMessageDialogClickListener =
            object : MessageAlertDialog.OnAlertMessageDialogClickListener {
                override fun onPositive() {
                    viewModel.deleteDiary(diaryModel = diaryModel)
                }

                override fun onNegative() {
                    messageAlertDialog.dismiss()
                }
            }
        if (!messageAlertDialog.isAdded) {
            messageAlertDialog.show(parentFragmentManager, "DeleteStory")
        }
    }

    override fun onRootItemClick(diaryModel: DiaryModel) {
        Toast.makeText(requireContext(), "stt:" + diaryModel.diaryId, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchData()
    }

    override fun onSavedDone() {
        mainActivity.showSnackBar(
            SnackBarType.Success,
            resources.getString(R.string.title_success),
            "Post Successfully"
        )
        viewModel.fetchData()
    }

}