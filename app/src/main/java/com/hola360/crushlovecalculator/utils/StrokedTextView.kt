package com.hola360.crushlovecalculator.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class StrokedTextView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    AppCompatTextView(context!!, attrs) {

    private var strokeColor = "#ffffff"
    private var mStrokeWidth = 2
    private var shadowColor = "#23898F"

    fun setStroke(color: String, width: Int) {
        strokeColor = color
        mStrokeWidth = width
    }

    fun setShadow(color: String) {
        shadowColor = color
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        paint.setShadowLayer(10f, 0f, -0f, Color.parseColor(shadowColor))
        super.onDraw(canvas)
        paint.apply {
            clearShadowLayer()
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val linearGradient = LinearGradient(
            0.0f, 0.0f, 0f, width.toFloat(),
            Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"), Shader.TileMode.CLAMP
        )
        paint.shader = linearGradient
        super.onDraw(canvas)
    }
}