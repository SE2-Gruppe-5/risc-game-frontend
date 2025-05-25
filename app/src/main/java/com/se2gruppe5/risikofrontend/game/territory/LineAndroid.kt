package com.se2gruppe5.risikofrontend.game.territory

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

const val LINE_STROKE_COLOR: Int = 0x000000
const val LINE_STROKE_WIDTH: Float = 5f

class LineAndroid: View, ILine {
    private val paint: Paint = Paint()
    private var startPoint: Pair<Float, Float> = Pair(0f, 0f)
    private var endPoint: Pair<Float, Float> = Pair(0f, 0f)

    constructor(context: Context, color: Int, strokeWidth: Float) : super(context) {
        init(color, strokeWidth)
    }

    constructor(context: Context) : super(context) {
        init(LINE_STROKE_COLOR, LINE_STROKE_WIDTH)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(LINE_STROKE_COLOR, LINE_STROKE_WIDTH)
    }


    private fun init(color: Int, strokeWidth: Float) {
        paint.color = color
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
    }

    override fun setColor(color: Int) {
        paint.color = color
        redraw()
    }

    override fun setWidth(width: Float) {
        require(width > 0) { "Invalid stroke width" }
        paint.strokeWidth = width
        redraw()
    }

    override fun setCoordinates(startPoint: Pair<Float, Float>, endPoint: Pair<Float, Float>) {
        this.startPoint = startPoint
        this.endPoint = endPoint
        redraw()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Early return - don't draw if selection is the same (i.e double click on same territory)
        if(startPoint == endPoint){
            return
        }
        canvas.drawLine(
            startPoint.first,
            startPoint.second,

            endPoint.first,
            endPoint.second,

            paint
        )
    }

    private fun redraw() {
        invalidate()
    }

}
