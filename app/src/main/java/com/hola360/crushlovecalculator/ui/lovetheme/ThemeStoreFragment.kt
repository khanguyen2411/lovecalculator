package com.hola360.crushlovecalculator.ui.lovetheme

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.model.theme.ThemeModel
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.data.utils.StoreDataResponse
import com.hola360.crushlovecalculator.databinding.FragmentThemeBinding
import com.hola360.crushlovecalculator.dialog.download.DownloadDialog
import com.hola360.crushlovecalculator.dialog.download.model.DownloadModel
import com.hola360.crushlovecalculator.ui.lovetheme.adapter.ThemeStoreAdapter
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.RootPath
import java.io.File


class ThemeStoreFragment :
    BaseViewModelFragment<FragmentThemeBinding>(), ThemeStoreAdapter.ListenerClickItem,
    DownloadDialog.OnDownloadFileListener {

    private val mAdapter = ThemeStoreAdapter()
    private lateinit var app: App
    private lateinit var viewModel: ThemeViewModel
    private var mLayoutManager: GridLayoutManager? = null
    private var currentId: String = ""
    private val downloadDlg by lazy {
        DownloadDialog.create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = requireActivity().application as App
        mAdapter.listener = this
    }

    override fun getLayout(): Int {
        return R.layout.fragment_theme
    }

    override fun initView() {
        binding!!.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        initLayoutManager()
        binding!!.recyleview.apply {
            setHasFixedSize(true)
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
        binding!!.mySwipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchData(true)
        }
        binding!!.viewModel = viewModel
        viewModel.fetchData(false)
    }

    private fun initLayoutManager() {
        mLayoutManager = GridLayoutManager(requireContext(), app.photoColumns).apply {
            this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (mAdapter.isHeader(position)) {
                        app.photoColumns
                    } else {
                        1
                    }
                }
            }
        }
    }

    private fun initObserveData() {
        viewModel.uiData.observe(this) {
            it?.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        enableRefreshLayout()
                        val imageStoreList = (it as StoreDataResponse.DataSuccessResponse).body
                        mAdapter.update(imageStoreList)
                    }
                    LoadDataStatus.REFRESH -> {
                        changeRefreshLayoutState(true)
                    }
                    LoadDataStatus.ERROR -> {
                        mAdapter.update(null)
                        changeRefreshLayoutState(false)
                    }
                    else -> {
                        changeRefreshLayoutState(false)
                    }
                }
            }
        }

        viewModel.installThemeLiveDate.observe(this) {
            it?.let {
                if (it is DataResponse.DataSuccessResponse) {
                    findNavController().navigate(
                        NavMainGraphDirections.actionGlobalThemePreviewFragment()
                            .setItemId(currentId)
                    )
                }
            }
        }
    }

    private fun enableRefreshLayout() {
        binding!!.mySwipeRefreshLayout.isEnabled = true
        binding!!.mySwipeRefreshLayout.isRefreshing = false
    }

    private fun changeRefreshLayoutState(refreshing: Boolean) {
        binding!!.mySwipeRefreshLayout.isEnabled = refreshing
        binding!!.mySwipeRefreshLayout.isRefreshing = refreshing
    }

    override fun onClickFileItem(position: Int, themeModel: ThemeModel) {
        if (!downloadDlg.isAdded) {
            val zipFile =
                File(RootPath.getInstance().getTempFolder(mainActivity), Constants.TEMP_ZIP_FILE)
            val downloadModel = DownloadModel(
                themeModel.itemId,
                getString(R.string.download_theme),
                themeModel.zip,
                themeModel.thumb,
                zipFile.absolutePath
            )
            viewModel.pushViewCount(themeModel.itemId)
            val bundle = Bundle()
            bundle.putParcelable(DownloadDialog.KEY_DATA, downloadModel)
            downloadDlg.arguments = bundle
            downloadDlg.onDownloadFileListener = this
            downloadDlg.show(parentFragmentManager, "DownloadTheme")
        }
    }

    override fun initViewModel() {
        val factory = ThemeViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[ThemeViewModel::class.java]
        initObserveData()
    }

    override fun onDownloadedSuccess() {}

    override fun onDownloadedSuccess(itemId: String?) {
        val zipFile =
            File(RootPath.getInstance().getTempFolder(mainActivity), Constants.TEMP_ZIP_FILE)
        viewModel.installPreviewTheme(zipFile)
        currentId = itemId!!
    }

}