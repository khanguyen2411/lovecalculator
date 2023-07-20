package com.hola360.crushlovecalculator.ui.myphoto

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.databinding.FragmentMyPhotoBinding
import com.hola360.crushlovecalculator.dialog.action.PickActionDiaLog
import com.hola360.crushlovecalculator.dialog.action.model.ActionModel
import com.hola360.crushlovecalculator.ui.myphoto.adapter.MyPhotoAdapter
import com.hola360.crushlovecalculator.ui.myphoto.base.BasePhotoFragment
import com.hola360.crushlovecalculator.utils.SystemUtils

class MyPhotoFragment : BasePhotoFragment<MyPhotoViewModel, FragmentMyPhotoBinding>(),
    PickActionDiaLog.OnActionPickerListener {
    private lateinit var app: App
    private val mAdapter: MyPhotoAdapter by lazy {
        MyPhotoAdapter()
    }

    private val actionTitle: ArrayList<String> by lazy {
        arrayListOf(
            getString(R.string.my_photo_dialog_newest),
            getString(R.string.my_photo_dialog_oldest)
        )
    }
    private val actionModelList: List<ActionModel> by lazy {
        val actions = actionTitle.flatMapIndexed { index, item ->
            val isSelected =
                (isOldestDateFirst && (index == 1)) || (!isOldestDateFirst && (index == 0))
            val actionsList = arrayListOf(ActionModel(-1, item, true, isSelected))
            actionsList
        }
        actions
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPress()
                }
            })
        mAdapter.callback = object : MyPhotoAdapter.ListenerClickItem {
            override fun onClickFileItem(pos: Int, localSongModel: LovePhotoModel) {
                val realPos = mAdapter.getRealPos(pos)
                findNavController().navigate(
                    MyPhotoFragmentDirections.actionToNavDetailPhoto(
                        realPos,
                        isOldestDateFirst
                    )
                )
            }

            override fun onUpdateCount() {
                countSelected()
                updateMenu()
            }
        }
        app = requireActivity().application as App
    }


    private fun resetNormalMode() {
        mAdapter.cancelSelectMode()
        countSelected()
        updateMenu()
    }

    private fun selectAll() {
        if (mAdapter.isSelectMode()) {
            val isSelectedAll: Boolean = mAdapter.isSelectedAll()
            mAdapter.selectAll(!isSelectedAll)
        } else {
            mAdapter.selectAll(true)
        }
    }

    private fun countSelected() {
        if (mAdapter.isSelectMode()) {
            val countSelected = mAdapter.countSelectedItem()
            if (countSelected > 0) {
                binding!!.toolbar.title =
                    resources.getQuantityString(R.plurals.item_size, countSelected, countSelected)
            } else {
                binding!!.toolbar.title = getString(R.string.select_items)
            }
            binding!!.toolbar.setNavigationIcon(R.drawable.ic_my_photo_close)
        } else {
            binding!!.toolbar.title = getString(R.string.my_photo_title)
            binding!!.toolbar.setNavigationIcon(R.drawable.ic_toolbar_back)
        }
    }


    private fun updateMenu() {
        binding!!.toolbar.menu.clear()
        if (mAdapter.isSelectMode()) {
            binding!!.toolbar.inflateMenu(R.menu.menu_photo_select_mode)
            updateSelectedItem()
        } else {
            binding!!.toolbar.inflateMenu(R.menu.menu_photo_normal)
        }
    }

    private fun updateSelectedItem() {
        if (mAdapter.isSelectMode()) {
            val selectItem = binding!!.toolbar.menu.findItem(R.id.action_select_all)
            if (selectItem != null) {
                val isSelectedAll: Boolean = mAdapter.isSelectedAll()
                if (isSelectedAll) {
                    selectItem.setIcon(R.drawable.ic_my_photo_select_all)
                } else {
                    selectItem.setIcon(R.drawable.ic_my_photo_unselect_all)
                }
            }
        }
    }

    override fun initView() {
        updateMenu()
        binding!!.toolbar.setNavigationOnClickListener { onBackPress() }
        binding!!.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_sort -> {
                    showSortDialog()
                }
                R.id.action_select_all -> {
                    selectAll()
                }
                R.id.action_delete -> {
                    deleteFiles()
                }
                R.id.action_share -> {
                    share()
                }
            }
            true
        }
        binding!!.myPhotoRv.setHasFixedSize(false)
        binding!!.myPhotoRv.layoutManager = GridLayoutManager(requireContext(),app.photoColumns+1).apply {
            this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (mAdapter.isHeader(position)) {
                        app.photoColumns+1
                    } else {
                        1
                    }
                }
            }
        }
        binding!!.myPhotoRv.adapter = mAdapter
        binding!!.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        viewModel!!.fetchAllPhotos(mainActivity, isOldestDateFirst, false)
    }

    private fun onBackPress() {
        if (mAdapter.isSelectMode()) {
            resetNormalMode()
        } else {
            findNavController(binding!!.root).navigateUp()

        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_my_photo
    }

    override fun initViewModel() {
        val factory = MyPhotoViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[MyPhotoViewModel::class.java]
        observeData()
    }

    private fun showSortDialog() {
        actionModelList.mapIndexed { index, item ->
            item.selected =
                (isOldestDateFirst && (index == 1)) || (!isOldestDateFirst && (index == 0))

        }
        val pickActionDiaLog = PickActionDiaLog.create(
            getString(R.string.action_sort_by),
            ArrayList(actionModelList)
        )
        pickActionDiaLog.onActionPickerListener = this
        pickActionDiaLog.show(requireActivity().supportFragmentManager, "sortDialog")
    }


    override fun getSelectedItem() {
        selectedItemList.clear()
        if (mAdapter.isSelectMode()) {
            val temp = mAdapter.getAllSelectedFiles()
            if (temp.isNotEmpty()) {
                selectedItemList.addAll(temp)
            }
        }
    }

    override fun onDeleteSuccess() {
        resetNormalMode()
    }

    override fun onCancelDelete() {
        resetNormalMode()
    }

    override fun onFetchDataSuccess(data: MutableList<LovePhotoModel>) {
        mAdapter.updateData(data)
    }

    override fun onFetchDataError() {
        mAdapter.updateData(null)
    }

    override fun onItemSelected(itemPos: Int) {
        isOldestDateFirst = (itemPos == 1)
        viewModel!!.fetchAllPhotos(mainActivity, isOldestDateFirst, false)
    }

    override fun onDismiss() {

    }
}