package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.enums.Continent
import java.util.UUID

data class TerritoryRecord (
    val id: Int,
    var stat: Int,
    var continent: Continent,
    val position: Pair<Int, Int>,
    val size: Pair<Int, Int>,
) {
    val connections: MutableList<TerritoryRecord> = ArrayList()
    var owner: UUID? = null

    fun getCenter(): Pair<Int, Int> {
        return Pair(position.first + size.first/2, position.second + size.second/2)
    }
}
