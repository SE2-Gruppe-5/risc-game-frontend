package com.se2gruppe5.risikofrontend.game.territory

interface ILine {
    fun setColor(color: Int)
    fun setWidth(width: Float)
    fun setCoordinates(startPoint: Pair<Float, Float>, endPoint: Pair<Float, Float>)
}