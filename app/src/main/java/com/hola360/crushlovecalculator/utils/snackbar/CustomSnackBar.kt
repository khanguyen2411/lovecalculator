package com.hola360.crushlovecalculator.utils.snackbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hola360.crushlovecalculator.R

class CustomSnackBar(parent: ViewGroup, content: CustomSnackBarView) : BaseTransientBottomBar<CustomSnackBar>(parent, content, content) {
    init {
        getView().setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
        getView().setPadding(0, 0, 0, 0);
    }

    companion object {
        private val DEEP_COLORS = intArrayOf(R.color.md_green_500, R.color.md_amber_500, R.color.md_red_500)
        private val BG_COLORS = intArrayOf(R.color.md_green_100, R.color.md_amber_100, R.color.md_red_100)
        private val ICONS = intArrayOf(
                 R.drawable.ic_snackbar_success,
                R.drawable.ic_snackbar_warning,
                R.drawable.ic_snackbar_error
            )

        fun make(viewGroup: ViewGroup, anchorView: View, snackBarType: SnackBarType, title: String, message: String): CustomSnackBar {
            val snackBar: CustomSnackBar
            val customView = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_snackbar_custom, viewGroup, false) as CustomSnackBarView
            customView.tvTitle.text = title
            customView.tvMessage.text = message

            customView.viewHeader.setColorFilter(ContextCompat.getColor(viewGroup.context, DEEP_COLORS[snackBarType.ordinal]))
            customView.btClose.setColorFilter(ContextCompat.getColor(viewGroup.context, DEEP_COLORS[snackBarType.ordinal]))
            customView.cardBackground.setCardBackgroundColor(ContextCompat.getColor(viewGroup.context, BG_COLORS[snackBarType.ordinal]))
            customView.imMessageType.setColorFilter(ContextCompat.getColor(viewGroup.context,DEEP_COLORS[snackBarType.ordinal]))
            customView.imMessageType.setImageResource(ICONS[snackBarType.ordinal])
            snackBar = CustomSnackBar(viewGroup, customView)
            snackBar.duration = Snackbar.LENGTH_LONG
            snackBar.anchorView = anchorView
            customView.btClose.setOnClickListener {
                snackBar.dismiss()
            }
            return snackBar
        }
    }
}