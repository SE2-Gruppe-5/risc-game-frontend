package com.se2gruppe5.risikofrontend.game.territory

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import kotlin.Float

/**
 * Custom View Class for painting pointing arrows
 */
class PointingArrowAndroid : View, IPointingArrowUI{
    //Alternatively use @JvmOverloads for less boilerplate and more Kotlin-Idiomatic solution.
    //Coming from Java, i prefer this for readability. (Albeit a bit more verbose)
    //Note: Language-level rule of Kotlin that "Constructors must delegate directly", still applies.
    //(Hence the inline call to super)

    private val arrowPaint = Paint()
    private var startPoint: Pair<Float, Float> = Pair(0f, 0f)
    private var endPoint: Pair<Float, Float> = Pair(10f, 10f)


    //Secondary Constructor for in-code initialization
    constructor(context: Context, color: Int, strokeWidth: Float) : super(context) {
        init(color, strokeWidth)
    }
    constructor(context: Context) : super(context) {
        init("#FF0000".toColorInt(), 10f)
    }

    //Secondary Constructor for XML initialization
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init("#FF0000".toColorInt(), 10f)
        //XML-initialization cannot pass parameters programmatically, will always be default
        //Certainly possible via some properties... But certainly not worth it
    }

    //Shared Logic for Both kinds of constructor calls
    private fun init(color: Int, strokeWidth: Float) {
        //Initialize "Paint" (= Settings for the used strokes)
        arrowPaint.color = color
        arrowPaint.strokeWidth = strokeWidth
        arrowPaint.style = Paint.Style.STROKE
        arrowPaint.isAntiAlias = true
    }

    override fun setColor(color: Int) {
        arrowPaint.color = color
    }

    override fun setWidth(width: Float) {
        if (width <= 0) {
            throw IllegalArgumentException("Invalid stroke-width")
        }
        arrowPaint.strokeWidth = width
    }

    override fun setCoordinates(startPoint: Pair<Float, Float>, endPoint: Pair<Float, Float>) {
        //todo assert wether values are valid maybe(?)
        this.startPoint = startPoint
        this.endPoint = endPoint
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(
            startPoint.first,
            startPoint.second,

            endPoint.first,
            endPoint.second,

            arrowPaint
        )
        invalidate() //<- force redraw
        //todo: arrow head or fancy arc?
    }

}
