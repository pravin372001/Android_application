package com.example.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CircularImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        val path = Path()
        val radius = width.coerceAtMost(height) / 2.toFloat()
        path.addCircle(width / 2.toFloat(), height / 2.toFloat(), radius, Path.Direction.CCW)
        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}
