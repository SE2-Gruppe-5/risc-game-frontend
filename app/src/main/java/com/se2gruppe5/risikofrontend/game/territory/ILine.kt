package com.se2gruppe5.risikofrontend.game.territory

import com.se2gruppe5.risikofrontend.game.dataclasses.util.Point2D

interface ILine {
    fun setColor(color: Int)
    fun setWidth(width: Float)
    fun setCoordinates(startPoint: Point2D, endPoint: Point2D)
}
