package com.hola360.crushlovecalculator.utils.listener

import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

class SafeMenuClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeClick: (MenuItem?) -> Unit
) : Toolbar.OnMenuItemClickListener {
    private var lastTimeClicked: Long = 0
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        val recentClickTime: Long = lastTimeClicked
        lastTimeClicked = System.currentTimeMillis()
        return if (System.currentTimeMillis() - recentClickTime < defaultInterval) {
            false
        } else {
            onSafeClick(item)
            true
        }
    }
}