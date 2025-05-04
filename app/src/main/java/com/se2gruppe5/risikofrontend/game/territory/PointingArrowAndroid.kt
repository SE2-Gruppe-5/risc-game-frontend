package com.se2gruppe5.risikofrontend.game.territory

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.Float
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

const val strokeColor: Int = 0xFF0000
const val strokeWidth: Float = 10f
const val arrowHeadLength: Float = 50f
const val arrowHeadAngle: Float = 35f

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
    private var endPoint: Pair<Float, Float> = Pair(0f, 0f)


    //Secondary Constructor for in-code initialization
    constructor(context: Context, color: Int, strokeWidth: Float) : super(context) {
        init(color, strokeWidth)
    }
    constructor(context: Context) : super(context) {
        init(strokeColor, strokeWidth)
    }

    //Secondary Constructor for XML initialization
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(strokeColor, strokeWidth)
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
        arrowPaint.strokeCap = Paint.Cap.ROUND
        arrowPaint.strokeJoin = Paint.Join.ROUND
    }

    override fun setColor(color: Int) {
        arrowPaint.color = color
        redraw()
    }

    override fun setWidth(width: Float) {
        if (width <= 0) {
            throw IllegalArgumentException("Invalid stroke-width")
        }
        arrowPaint.strokeWidth = width
        redraw()
    }

    override fun setCoordinates(startPoint: Pair<Float, Float>, endPoint: Pair<Float, Float>) {
        //todo assert whether values are valid maybe(?)
        this.startPoint = startPoint
        this.endPoint = endPoint
        redraw()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Early return - don't draw if selection is the same (i.e double click on same territory)
        if(startPoint==endPoint){
            return
        }
        canvas.drawLine(
            startPoint.first,
            startPoint.second,

            endPoint.first,
            endPoint.second,

            arrowPaint
        )
        drawFancyArrowHead(canvas)

    }
    private fun redraw(){
        invalidate() //<- force redraw
    }
    private fun drawFancyArrowHead(canvas: Canvas){
        val arrowDirectionAngle = atan2(
            (endPoint.second - startPoint.second).toDouble(),
            (endPoint.first - startPoint.first).toDouble()
        )
        val angleOffset = Math.toRadians(arrowHeadAngle.toDouble())

        val x0 = endPoint.first - arrowHeadLength * cos(arrowDirectionAngle - angleOffset).toFloat()
        val y0 = endPoint.second - arrowHeadLength * sin(arrowDirectionAngle - angleOffset).toFloat()
        val x1 = endPoint.first - arrowHeadLength * cos(arrowDirectionAngle + angleOffset).toFloat()
        val y1 = endPoint.second - arrowHeadLength * sin(arrowDirectionAngle + angleOffset).toFloat()

        canvas.drawLine(endPoint.first, endPoint.second, x0, y0, arrowPaint)
        canvas.drawLine(endPoint.first, endPoint.second, x1, y1, arrowPaint)

    }

}
