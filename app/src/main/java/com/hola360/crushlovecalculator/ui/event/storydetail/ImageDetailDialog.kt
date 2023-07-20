package com.hola360.crushlovecalculator.ui.event.storydetail

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.doOnLayout
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseFullAlertDialog
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.databinding.DialogStoryDetailBinding
import com.hola360.crushlovecalculator.utils.clickWithDebounce

class ImageDetailDialog : BaseFullAlertDialog<DialogStoryDetailBinding>() {

    private var currentPosition: Int? = null
    private var imageList: MutableList<PhotoModel>? = null
    private val mAdapter = ViewPagerAdapter()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog!!.window!!.setDimAmount(0.8F)
    }
F
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentPosition = requireArguments().getInt(KEY_CURRENT_POSITION)
        imageList = requireArguments().getParcelableArrayList(KEY_IMAGE_LIST)
    }

    override fun initView() {
        binding!!.apply {
            mAdapter.updateData(imageList!!)
            vpImage.apply {
                adapter = mAdapter
                offscreenPageLimit = 1
                doOnLayout {
                    setCurrentItem(currentPosition!!, true)
                }
            }
            btClose.clickWithDebounce {
                dismiss()
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.dialog_story_detail
    }

    companion object {
        private const val KEY_CURRENT_POSITION = "Key_Current_Position"
        private const val KEY_IMAGE_LIST = "Key_Image_List"

        fun create(position: Int, imageList: MutableList<PhotoModel>): ImageDetailDialog {
            val dialog = ImageDetailDialog()
            val bundle = Bundle()
            bundle.putInt(KEY_CURRENT_POSITION, position)
            bundle.putParcelableArrayList(KEY_IMAGE_LIST, ArrayList(imageList))
            dialog.arguments = bundle
            return dialog
        }
    }
}