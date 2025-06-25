package com.se2gruppe5.risikofrontend.layouts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.widget.FrameLayout
import com.se2gruppe5.risikofrontend.R
import kotlin.math.max
import androidx.core.graphics.toColorInt

class BackgroundFrameLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val drawable: Int = R.drawable.motherboard
    private val tint: Int = "#80ffffff".toColorInt()

    private val bitmap: Bitmap = BitmapFactory.decodeResource(resources, drawable)
    private val matrix = Matrix()
    private val paint: Paint = Paint()

    init {
        setWillNotDraw(false)
        paint.setColorFilter(PorterDuffColorFilter(tint, PorterDuff.Mode.SCREEN))
    }

    override fun onDraw(canvas: Canvas) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val bgWidth = bitmap.width.toFloat()
        val bgHeight = bitmap.height.toFloat()

        val scale = max(viewWidth / bgWidth, viewHeight / bgHeight)
        val offsetX = (viewWidth - bgWidth * scale) * 0.5f
        val offsetY = (viewHeight - bgHeight * scale) * 0.5f

        matrix.reset()
        matrix.postScale(scale, scale)
        matrix.postTranslate(offsetX, offsetY)

        canvas.drawBitmap(bitmap, matrix, paint)

        super.onDraw(canvas)
    }
}
