package com.se2gruppe5.risikofrontend.game.territory

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Point2D
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom View Class for painting pointing arrows
 */
class PointingArrowAndroid : View, IPointingArrowUI{
    //Alternatively use @JvmOverloads for less boilerplate and more Kotlin-Idiomatic solution.
    //Coming from Java, i prefer this for readability. (Albeit a bit more verbose)
    //Note: Language-level rule of Kotlin that "Constructors must delegate directly", still applies.
    //(Hence the inline call to super)
    companion object{
        private const val POINTING_ARROW_STROKE_COLOR: Int = Color.RED
        private const val POINTING_ARROW_STROKE_WIDTH: Float = 10f
        private const val POINTING_ARROW_ARROWHEAD_LENGTH: Float = 50f
        private const val POINTING_ARROW_ARROWHEAD_ANGLE: Float = 35f
    }

    private val arrowPaint = Paint()
    private var startPoint: Point2D = Point2D(0f,0f)
    private var endPoint: Point2D = Point2D(0f,0f)


    //Secondary Constructor for in-code initialization
    constructor(context: Context, color: Int, strokeWidth: Float) : super(context) {
        init(color, strokeWidth)
    }
    constructor(context: Context) : super(context) {
        init(POINTING_ARROW_STROKE_COLOR, POINTING_ARROW_STROKE_WIDTH)
    }

    //Secondary Constructor for XML initialization
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(POINTING_ARROW_STROKE_COLOR, POINTING_ARROW_STROKE_WIDTH)
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
        require(width > 0) { "Invalid stroke-width" }

        arrowPaint.strokeWidth = width
        redraw()
    }

    override fun setCoordinates(startPoint: Point2D, endPoint: Point2D) {
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
            startPoint.x,
            startPoint.y,

            endPoint.x,
            endPoint.y,

            arrowPaint
        )
        drawFancyArrowHead(canvas)

    }
    private fun redraw(){
        invalidate() //<- force redraw
    }
    private fun drawFancyArrowHead(canvas: Canvas){
        val arrowDirectionAngle = atan2(
            (endPoint.y - startPoint.y).toDouble(),
            (endPoint.x - startPoint.x).toDouble()
        )
        val angleOffset = Math.toRadians(POINTING_ARROW_ARROWHEAD_ANGLE.toDouble())

        val x0 = endPoint.x - POINTING_ARROW_ARROWHEAD_LENGTH * cos(arrowDirectionAngle - angleOffset).toFloat()
        val y0 = endPoint.y - POINTING_ARROW_ARROWHEAD_LENGTH * sin(arrowDirectionAngle - angleOffset).toFloat()
        val x1 = endPoint.x - POINTING_ARROW_ARROWHEAD_LENGTH * cos(arrowDirectionAngle + angleOffset).toFloat()
        val y1 = endPoint.y - POINTING_ARROW_ARROWHEAD_LENGTH * sin(arrowDirectionAngle + angleOffset).toFloat()

        canvas.drawLine(endPoint.x, endPoint.y, x0, y0, arrowPaint)
        canvas.drawLine(endPoint.x, endPoint.y, x1, y1, arrowPaint)

    }

}
