package com.hola360.crushlovecalculator.ui.lovediary.search

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.databinding.FragmentSearchDiaryBinding
import com.hola360.crushlovecalculator.ui.lovediary.search.adapter.ResultDiaryAdapter
import com.hola360.crushlovecalculator.utils.ToastUtils
import com.hola360.crushlovecalculator.utils.clickWithDebounce

class SearchDiaryFragment : BaseViewModelFragment<FragmentSearchDiaryBinding>(),
    ResultDiaryAdapter.OnItemClickListener {

    private lateinit var mViewModel: SearchDiaryViewModel
    private var curRecyclerViewScrollState: Int = RecyclerView.SCROLL_STATE_IDLE
    private val mAdapter = ResultDiaryAdapter()

    override fun initView() {
        binding!!.apply {
            viewModel = mViewModel
            ivNavigation.clickWithDebounce { findNavController().navigateUp() }

            ivClose.clickWithDebounce { etSearch.text.clear() }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0!!.trim().toString().isNotEmpty()) {
                        ivClose.visibility = View.VISIBLE
                        llHelp.visibility = View.GONE
                        rvDiary.visibility = View.VISIBLE
                    } else {
                        ivClose.visibility = View.INVISIBLE
                        llHelp.visibility = View.VISIBLE
                        rvDiary.visibility = View.INVISIBLE
                        llNoResult.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    val keyword = p0?.trim().toString()
                    if (keyword.isNotBlank() && curRecyclerViewScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                        mViewModel.fetchDiary(keyword)
                    }
                }
            })

            rvDiary.apply {
                adapter = mAdapter
                layoutManager =
                    LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        curRecyclerViewScrollState = newState
                    }
                })
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_search_diary
    }

    override fun initViewModel() {
        val factory = SearchDiaryViewModel.Factory(mainActivity.application)
        mViewModel = ViewModelProvider(this, factory)[SearchDiaryViewModel::class.java]

        mViewModel.mDiaryList.observe(this) {
            it?.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        val data = (it as DataResponse.DataSuccessResponse).body
                        mAdapter.updateData(data)
                        binding!!.llHelp.visibility = View.GONE
                    }
                    LoadDataStatus.ERROR -> {
                        mAdapter.clearData()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDiaryItemClickListener(diaryModel: DiaryModel) {
        ToastUtils.getInstance(mainActivity).showToast(diaryModel.diaryId.toString())
    }
}