package com.se2gruppe5.risikofrontend.game.territory

import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord


interface ITerritoryVisual {
    val territoryRecord: TerritoryRecord
    fun highlightSelected()
    fun changeColor(color: Int)
    //fun changeStat() //todo maybe needed?
    fun getCoordinatesAsFloat(): Pair<Float, Float>
}