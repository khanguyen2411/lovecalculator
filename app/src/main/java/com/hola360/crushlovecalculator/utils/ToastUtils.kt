package com.hola360.crushlovecalculator.utils

import android.content.Context
import android.widget.Toast

class ToastUtils private constructor(context: Context) {
    private val toast: Toast
    fun showToast(message: String?) {
        toast.setText(message)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    companion object {
        private var instance: ToastUtils? = null
        fun getInstance(context: Context): ToastUtils {
            if (instance == null) {
                instance = ToastUtils(context)
            }
            return instance!!
        }

        fun release() {
            if (instance != null) {
                instance = null
            }
        }
    }

    init {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    }
}