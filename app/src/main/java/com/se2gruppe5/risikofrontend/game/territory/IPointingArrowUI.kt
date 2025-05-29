package com.se2gruppe5.risikofrontend.game.territory

import com.se2gruppe5.risikofrontend.game.engine.Point2D

interface IPointingArrowUI {
    fun setColor(color: Int)
    fun setWidth(width: Float)
    fun setCoordinates(startPoint: Point2D, endPoint: Point2D)
}
