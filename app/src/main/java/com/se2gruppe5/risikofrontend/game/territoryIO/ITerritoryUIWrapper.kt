package com.se2gruppe5.risikofrontend.game.territoryIO

import com.se2gruppe5.risikofrontend.game.dataclasses.Territory


interface ITerritoryUIWrapper {
    val territory: Territory
    fun highlightSelected()
    fun changeColor(color: Int)
    fun changeStat()
    fun subscribeLambda(lambda: (ITerritoryUIWrapper) -> Unit)
}