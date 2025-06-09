package com.se2gruppe5.risikofrontend.game.board

import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Point2D
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Size2D
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Transform2D
import kotlinx.serialization.json.Json


class BoardLoader(jsonSrc: String) {
    val territories: List<TerritoryRecord> = loadTerritories(jsonSrc)

    private fun loadTerritories(json: String): List<TerritoryRecord> {
        val boardData = Json.decodeFromString<BoardData>(json)

        val territories: HashMap<Int, TerritoryRecord> = HashMap()
        val jsonTerritories = boardData.territories

        for(t in jsonTerritories) {
            val transform = Transform2D( //todo maybe change json to actually use float
                Point2D(t.position.x.toFloat(),t.position.y.toFloat()),
                Size2D(t.size.x.toFloat(),t.size.y.toFloat()))


            territories[t.id] = TerritoryRecord(t.id, 0, t.continent, transform)
        }

        val jsonConnections = boardData.connections
        for(conn in jsonConnections) {
            val territoryOrigin = territories[conn[0]]

            for(i in 1 until conn.size) {
                val territoryTarget = territories[conn[i]]

                // Don't add duplicate connections
                if(territoryTarget != null && territoryOrigin != null
                    && !territoryOrigin.connections.contains(territoryTarget)) {
                    territoryOrigin.connections.add(territoryTarget)
                    territoryTarget.connections.add(territoryOrigin)
                }
            }
        }

        return territories.values.toList()
    }
}
