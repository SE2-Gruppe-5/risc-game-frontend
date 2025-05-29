package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.engine.Point2D
import com.se2gruppe5.risikofrontend.game.engine.Transform2D
import com.se2gruppe5.risikofrontend.game.enums.Continent
import java.util.UUID

data class TerritoryRecord (
    val id: Int,
    var stat: Int,
    val continent: Continent,
    val transform: Transform2D
) {
    val connections: MutableList<TerritoryRecord> = ArrayList()
    var owner: UUID? = null

    fun getCenter(): Point2D {
        val centerX: Float = transform.position.x+transform.scale.x/2
        val centerY: Float = transform.position.y+transform.scale.y/2
        return Point2D(centerX,centerY)
    }

    fun isConnected(t: TerritoryRecord): Boolean {
        return connections.contains(t)
    }
}
