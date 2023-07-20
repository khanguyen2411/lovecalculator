package com.hola360.crushlovecalculator.dialog.jobdialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseAlertDialog
import com.hola360.crushlovecalculator.databinding.LayoutNationDialogBinding
import com.hola360.crushlovecalculator.dialog.nationdialog.NationDialog
import com.hola360.crushlovecalculator.dialog.nationdialog.NationUtils

class JobDialog : BaseAlertDialog<LayoutNationDialogBinding>() {

    private val adapter: JobAdapter by lazy {
        JobAdapter(requireContext(), object : JobAdapter.JobOnclick {
            override fun onclick(job: String) {
                dialog?.dismiss()
                if (findNavController().currentDestination?.id == curOwnerId) {
                    callback!!.onSelectItem(job)
                }
            }
        })
    }
    private val jobList: MutableList<String> by lazy {
        getListJob(requireContext())
    }
    private var curFilter = ""
    private var curRecyclerViewScrollState: Int = RecyclerView.SCROLL_STATE_IDLE
    private var selectPos: Int = 84
    private var callback: NationDialog.NationDialogCallback? = null

    override fun initView() {
        binding!!.isNation = false
        binding!!.nationRv.setHasFixedSize(true)
        binding!!.nationRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.setSelectPosition(selectPos)
        binding!!.nationRv.adapter = adapter
        binding!!.nationRv.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                curRecyclerViewScrollState= newState
            }
        })
        adapter.updateData(jobList)
        binding!!.edtNationFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.toString() != curFilter && curRecyclerViewScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    adapter.filter.filter(s?.toString())
                    curFilter = s?.toString().toString()
                }
            }
        })
    }

    fun setSelectPosition(selectPos: Int) {
        this.selectPos = selectPos
    }

    override fun onStop() {
        super.onStop()
        binding!!.edtNationFilter.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()
        callback?.onDismiss()
    }

    companion object {
        fun getListJob(context: Context): MutableList<String> {
            val jsonString = NationUtils.readJsonFile(context, "job", "job_list.json")
            val listJob = Gson().fromJson(jsonString, JobList::class.java)
            return if (listJob.jobList != null) {
                listJob.jobList!!
            } else {
                mutableListOf<String>()
            }
        }

        fun create(callback: NationDialog.NationDialogCallback): JobDialog {
            val jobDialog = JobDialog()
            jobDialog.callback = callback
            return jobDialog
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_nation_dialog
    }

}