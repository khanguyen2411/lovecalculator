package com.hola360.crushlovecalculator.utils.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.LayoutBottomToastBinding

class BottomToastUtils constructor(contexts: Context){
    private var instance: BottomToastUtils? = null
    private var toast: Toast? = null
    private var context: Context? = null
    private var binding: LayoutBottomToastBinding? = null
    private val DEEP_COLORS =
        intArrayOf(R.color.md_green_500, R.color.md_amber_500, R.color.md_red_500)
    private val BG_COLORS =
        intArrayOf(R.color.md_green_100, R.color.color_custom_waring, R.color.md_red_100)
    private val ICONS = intArrayOf(
        R.drawable.ic_snackbar_success,
        R.drawable.ic_snackbar_warning,
        R.drawable.ic_snackbar_error
    )
    init {
        context = contexts
        binding = LayoutBottomToastBinding.inflate(LayoutInflater.from(context), null, false)
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
        toast!!.setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 120)
        toast!!.setView(binding!!.getRoot())
        toast!!.setDuration(Toast.LENGTH_SHORT)
    }

    fun getInstance(context: Context): BottomToastUtils? {
        if (instance == null) {
            instance = BottomToastUtils(context)
        }
        return instance
    }
    fun showToast(messageType: MessageType, title: String?, message: String?) {
        binding!!.tvTitle.setText(title)
        binding!!.tvMsg.setText(message)
        applyTheme(binding, messageType)
        toast!!.show()
    }

    private fun applyTheme(binding: LayoutBottomToastBinding?, messageType: MessageType) {
        binding!!.startSpace.setColorFilter(
            ContextCompat.getColor(
                context!!,
                DEEP_COLORS[messageType.ordinal]
            )
        )
        binding.cardToast.setCardBackgroundColor(
            ContextCompat.getColor(
                context!!,
                BG_COLORS[messageType.ordinal]
            )
        )
        binding.imMessageType.setColorFilter(
            ContextCompat.getColor(
                context!!,
                DEEP_COLORS[messageType.ordinal]
            )
        )
        binding.imMessageType.setImageResource(ICONS[messageType.ordinal])
    }

    fun release() {
        if (instance != null) {
            instance = null
        }
    }

    enum class MessageType {
        Success, Warning, Error
    }
}