package com.hola360.crushlovecalculator.utils.snackbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.ContentViewCallback
import com.hola360.crushlovecalculator.R

class CustomSnackBarView constructor(context: Context, attr: AttributeSet?) :
    LinearLayout(context, attr, 0), ContentViewCallback {
    val tvTitle: TextView
    val tvMessage: TextView
    val btClose: ImageButton
    val viewHeader: ImageView
    val cardBackground: CardView
    val imMessageType: ImageView
    init {
        View.inflate(context,R.layout.layout_bottom_toast,this)
        this.tvTitle = findViewById(R.id.tvTitle)
        this.tvMessage = findViewById(R.id.tvMsg)
        this.btClose = findViewById(R.id.btClose)
        this.viewHeader = findViewById(R.id.startSpace)
        this.cardBackground = findViewById(R.id.cardToast)
        this.imMessageType = findViewById(R.id.imMessageType)
    }

    override fun animateContentIn(delay: Int, duration: Int) {

    }

    override fun animateContentOut(delay: Int, duration: Int) {

    }
}