package com.se2gruppe5.risikofrontend.game.territoryIO

interface ITerritoryUIWrapper {
    fun highlightSelected()
    fun changeColor(color: Int)
    fun changeStat()
    fun subscribeLambda(lambda: () -> Unit)
}