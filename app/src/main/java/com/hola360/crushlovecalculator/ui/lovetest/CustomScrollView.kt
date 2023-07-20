package com.hola360.crushlovecalculator.ui.lovetest

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class CustomScrollView constructor(context: Context, attr: AttributeSet) :
    NestedScrollView(context, attr) {

    private var scrollable = true

    fun setScrollable(scrollable: Boolean) {
        this.scrollable = scrollable
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                scrollable && super.onTouchEvent(ev)
            }
            else -> {
                super.onTouchEvent(ev)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return scrollable && super.onInterceptTouchEvent(ev)
    }

}