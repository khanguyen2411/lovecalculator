package com.hola360.crushlovecalculator.utils

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import com.hola360.crushlovecalculator.R

@RequiresApi(Build.VERSION_CODES.O)
class SGTextView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatTextView(context!!, attrs) {

    init{
        val typeFace = context!!.resources.getFont(R.font.quicksand_bold)
        typeface = typeFace
        textSize = 20F
    }

    private val strokePaint = Paint()
    private val gradientPaint = Paint()
    fun setStyle(
        strokeColor: String?, startColor: String?,
        endColor: String?, strokeWidthDp: Float, gradientHeightDp: Int
    ) {
        setStyle(
            Color.parseColor(strokeColor),
            dip2px(context, strokeWidthDp).toFloat(),
            LinearGradient(
                0f, 0f, 0f, dip2px(
                    context,
                    gradientHeightDp.toFloat()
                ).toFloat(), intArrayOf(
                    Color.parseColor(startColor),
                    Color.parseColor(endColor)
                ), null, TileMode.CLAMP
            )
        )
    }

    fun setStyle(strokeColor: Int, strokeWidth: Float, shader: Shader?) {
        strokePaint.isAntiAlias = true
        strokePaint.isDither = true
        strokePaint.isFilterBitmap = true
        strokePaint.strokeWidth = strokeWidth
        strokePaint.color = strokeColor
        strokePaint.strokeJoin = Paint.Join.ROUND
        strokePaint.strokeCap = Paint.Cap.ROUND
        strokePaint.style = Paint.Style.STROKE
        gradientPaint.isAntiAlias = true
        gradientPaint.isDither = true
        gradientPaint.isFilterBitmap = true
        gradientPaint.shader = shader
        gradientPaint.strokeJoin = Paint.Join.ROUND
        gradientPaint.strokeCap = Paint.Cap.ROUND
        gradientPaint.style = Paint.Style.FILL_AND_STROKE
        val textSize = textSize
        strokePaint.textSize = textSize
        gradientPaint.textSize = textSize
    }

    fun setShadowLayer(radius: Float, dx: Float, dy: Float, color: String?) {
        strokePaint.setShadowLayer(radius, dx, dy, Color.parseColor(color))
    }


    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        var width = measuredWidth
        if (width == 0) {
            measure(0, 0)
            width = (measuredWidth + strokePaint.strokeWidth * 2).toInt()
            setWidth(width)
        }
        val y = baseline.toFloat()
        val x = (width - strokePaint.measureText(text)) / 2
        canvas.drawText(text, x, y, strokePaint)
        canvas.drawText(text, x, y, gradientPaint)
    }

    companion object {
        private fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }
    }
}