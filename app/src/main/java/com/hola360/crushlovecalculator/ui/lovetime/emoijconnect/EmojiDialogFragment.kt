package com.hola360.crushlovecalculator.ui.lovetime.emoijconnect

import android.app.Application
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelDialogFragment
import com.hola360.crushlovecalculator.data.model.EmoijConnect
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.databinding.EmoijConnectDialogFragmentBinding
import com.hola360.crushlovecalculator.dialog.download.DownloadDialog
import com.hola360.crushlovecalculator.dialog.download.model.DownloadModel
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.RootPath
import java.io.File

class EmojiDialogFragment : BaseViewModelDialogFragment<EmoijConnectDialogFragmentBinding>(),
    DownloadDialog.OnDownloadFileListener {
    lateinit var viewModel: EmojiViewModel
    var mAdapter = EmojiAdapter()
    private var layoutGridLayoutManager: GridLayoutManager? = null
    var onSetEmojiSuccessListener: OnSetEmojiSuccessListener? = null
    private val downloadDlg by lazy {
        DownloadDialog.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter.onEmojiItemSelectedListener = object : EmojiAdapter.OnEmojiItemSelectedListener {
            override fun onSelected(position: Int, emoijConnect: EmoijConnect) {
                if (!downloadDlg.isAdded) {
                    val emojiFile =
                        File(
                            RootPath.getInstance().getThemeFolder(mainActivity),
                            Constants.THEME_EMOJI
                        )
                    val downloadModel = DownloadModel(
                        null,
                        getString(R.string.msg_select_emoji_connection),
                        emoijConnect.svg,
                        emoijConnect.png,
                        emojiFile.absolutePath
                    )
                    val bundle = Bundle()
                    bundle.putParcelable(DownloadDialog.KEY_DATA, downloadModel)
                    downloadDlg.arguments = bundle
                    downloadDlg.onDownloadFileListener = this@EmojiDialogFragment
                    downloadDlg.show(parentFragmentManager, "DownloadTheme")
                }
            }

        }
    }

    override fun initView() {
        layoutGridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding!!.toolbar.setNavigationOnClickListener {
            onDismis()
        }
        binding!!.recycleview.apply {
            setHasFixedSize(true)
            adapter = mAdapter
            layoutManager = layoutGridLayoutManager
        }

        binding!!.mySwipeRefreshLayout.setOnRefreshListener {
            binding!!.mySwipeRefreshLayout.isEnabled = true
            binding!!.mySwipeRefreshLayout.isRefreshing = true
            viewModel.fetchData(false)
        }
        binding!!.viewModel = viewModel
        viewModel.fetchData(true)
    }

    private fun onDismis() {
        dismiss()
    }

    override fun initViewModel() {
        val factory = EmojiViewModel.Factory(requireActivity().application as Application)
        viewModel = ViewModelProvider(this, factory)[EmojiViewModel::class.java]
        viewModel.uiData.observe(this) {
            it?.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        binding!!.mySwipeRefreshLayout.isEnabled = true
                        binding!!.mySwipeRefreshLayout.isRefreshing = false
                        val listEmoij = (it as DataResponse.DataSuccessResponse).body
                        mAdapter.updateData(listEmoij)
                    }
                    else -> {
                        binding!!.mySwipeRefreshLayout.isEnabled = true
                        binding!!.mySwipeRefreshLayout.isRefreshing = false
                        mAdapter.updateData(null)
                    }
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

    override fun getLayout(): Int {
        return R.layout.emoij_connect_dialog_fragment
    }

    companion object {
        fun create(): EmojiDialogFragment {
            return EmojiDialogFragment()
        }

    }

    interface OnSetEmojiSuccessListener {
        fun onSetEmojiSuccess()
    }

    override fun onDownloadedSuccess() {}

    override fun onDownloadedSuccess(itemId: String?) {
        dismiss()
        onSetEmojiSuccessListener?.onSetEmojiSuccess()
    }
}