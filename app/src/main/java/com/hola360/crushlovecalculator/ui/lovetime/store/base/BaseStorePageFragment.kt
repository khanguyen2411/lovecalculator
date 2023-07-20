package com.hola360.crushlovecalculator.ui.lovetime.store.base

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.model.ImageStore
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.data.utils.StoreDataResponse
import com.hola360.crushlovecalculator.databinding.FragmentImageStorePageBinding
import com.hola360.crushlovecalculator.ui.lovetime.store.ImageStoreFragment
import com.hola360.crushlovecalculator.ui.lovetime.store.adapter.ImageStoreAdapter
import com.hola360.crushlovecalculator.ui.lovetime.store.detail.ImageStoreDetailDialog
import com.hola360.crushlovecalculator.ui.lovetime.store.favorite.FavoriteImageViewModel
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType

abstract class BaseStorePageFragment<V : BaseStoreViewModel> :
    BaseViewModelFragment<FragmentImageStorePageBinding>(), ImageStoreAdapter.ListenerClickItem {
    private val mAdapter = ImageStoreAdapter()
    private lateinit var app: App
    protected lateinit var viewModel: V
    private var mLayoutManager: GridLayoutManager? = null
    private var firstVisibleItem = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var visibleThreshold = 1
    private var imageStoreFragment: ImageStoreFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = requireActivity().application as App
        imageStoreFragment = (parentFragment as ImageStoreFragment)
        mAdapter.listener = this
    }

    override fun getLayout(): Int {
        return R.layout.fragment_image_store_page
    }

    override fun initView() {
        initLayoutManager()
        binding!!.recyleview.apply {
            setHasFixedSize(true)
            adapter = mAdapter
            layoutManager = mLayoutManager
            if (viewModel !is FavoriteImageViewModel) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        visibleItemCount = recyclerView.childCount
                        totalItemCount = mLayoutManager!!.itemCount
                        firstVisibleItem = mLayoutManager!!.findFirstVisibleItemPosition()
                        if (dy > 0 && !binding!!.recyleview.canScrollVertically(1)) {
                            viewModel.fetchData(true)
                        }
                    }
                })
            }
        }
        binding!!.mySwipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchData(false)
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

    protected fun initObserveData() {
        viewModel.uiData.observe(this) {
            it?.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        enableRefreshLayout()
                        val imageStoreList = (it as StoreDataResponse.DataSuccessResponse).body
                        val curPage = it.curPage
                        mAdapter.update((curPage > 1), imageStoreList)
                    }
                    LoadDataStatus.REFRESH -> {
                        changeRefreshLayoutState(true)
                    }
                    LoadDataStatus.ERROR -> {
                        val curPage = (it as StoreDataResponse.DataErrorResponse).curPage
                        mAdapter.update((curPage > 1), null)
                        if (curPage == 0) {
                            changeRefreshLayoutState(false)
                        } else {
                            enableRefreshLayout()
                        }
                    }
                    else -> {
                        changeRefreshLayoutState(false)
                    }
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

    override fun onClickFileItem(position: Int, imageStore: ImageStore) {
        val detailPhotoDialog = ImageStoreDetailDialog.create(imageStore)
        detailPhotoDialog.onSelectImageListener =
            object : ImageStoreDetailDialog.OnSelectImageListener {
                override fun onSelected() {
                    mainActivity.showSnackBar(
                        SnackBarType.Success, getString(R.string.title_success), getString(
                            R.string.set_photo_as_love_background
                        )
                    )
                    imageStoreFragment!!.findNavController().navigateUp()
                }

                override fun onDismiss() {
                    if (viewModel is FavoriteImageViewModel) {
                        viewModel.fetchData(false)
                    }
                }
            }
        detailPhotoDialog.show(parentFragmentManager, "DetailImageStore")
    }

    fun fetchData() {
        viewModel.fetchData(false)
    }
}