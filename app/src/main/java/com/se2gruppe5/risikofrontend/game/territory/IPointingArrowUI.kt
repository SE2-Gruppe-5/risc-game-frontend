package com.se2gruppe5.risikofrontend.game.territory

interface IPointingArrowUI {
    fun setColor(color: Int)
    fun setWidth(width: Float)
    fun setCoordinates(startPoint: Pair<Float, Float>, endPoint: Pair<Float, Float>)
}