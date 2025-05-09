package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.board.Continent

data class TerritoryRecord (
    val id: Int,
    var stat: Int,
    var continent: Continent,
    val position: Pair<Float, Float>,
    val size: Pair<Float, Float>,
) {
    val connections: MutableList<TerritoryRecord> = ArrayList()
    var owner: PlayerRecord? = null
}