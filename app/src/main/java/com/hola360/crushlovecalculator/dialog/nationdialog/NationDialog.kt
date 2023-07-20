package com.hola360.crushlovecalculator.dialog.nationdialog

import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseAlertDialog
import com.hola360.crushlovecalculator.databinding.LayoutNationDialogBinding

class NationDialog : BaseAlertDialog<LayoutNationDialogBinding>() {

    private val adapter: NationAdapter by lazy {
        NationAdapter(requireContext(), object : NationAdapter.NationOnclick {
            override fun onclick(nationCode: String) {
                dialog?.dismiss()
                if (findNavController().currentDestination?.id == curOwnerId) {
                    callback!!.onSelectItem(nationCode)
                    binding!!.edtNationFilter.setText("")
                    curFilter = ""
                }
            }
        })
    }
    private var curFilter = ""
    private var curRecyclerViewScrollState: Int = RecyclerView.SCROLL_STATE_IDLE
    private var selectPos: Int = 84
    private var callback: NationDialogCallback? = null

    override fun initView() {
        binding!!.isNation = true
        binding!!.nationRv.setHasFixedSize(true)
        binding!!.nationRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.setSelectPosition(selectPos)
        binding!!.nationRv.adapter = adapter
        binding!!.nationRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                curRecyclerViewScrollState = newState
            }
        })
        adapter.updateData(NationUtils.generateNationList(requireContext()))
        binding!!.edtNationFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val keyWord = s?.toString()
                if (keyWord != curFilter && curRecyclerViewScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    adapter.filter.filter(keyWord)
                    curFilter = keyWord!!
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

    interface NationDialogCallback {
        fun onSelectItem(nationCode: String)
        fun onDismiss()
    }

    companion object {
        fun create(callback: NationDialogCallback): NationDialog {
            val nationDialog = NationDialog()
            nationDialog.callback = callback
            return nationDialog
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_nation_dialog
    }

}