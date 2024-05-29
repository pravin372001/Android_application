package com.example.speedometer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class SpeedometerNeedleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var angle: Float = 135f // Start at 135 degrees (leftmost position)
    private val paint = Paint().apply {
        color = context.getColor(android.R.color.holo_red_dark)
        strokeWidth = 8f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    fun updateSpeed(speed: Float) {
        // Map speed (0 to 240) to angle (135 to 45)
        angle = 135f - (speed / 240f) * 90f // 90 degrees span
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - 20

        val endX = centerX + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
        val endY = centerY + radius * sin(Math.toRadians(angle.toDouble())).toFloat()

        canvas.drawLine(centerX, centerY, endX, endY, paint)
    }
}
