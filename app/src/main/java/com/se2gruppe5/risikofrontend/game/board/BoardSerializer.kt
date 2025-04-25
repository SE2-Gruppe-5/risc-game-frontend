package com.se2gruppe5.risikofrontend.game.board

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class BoardSerializer {
    fun loadTerritories(json: String): List<Territory> {
        val boardData = Json.decodeFromString<BoardData>(json)

        val territories: HashMap<Int, Territory> = HashMap()
        val jsonTerritories = boardData.territories

        for(t in jsonTerritories) {
            val position: Pair<Float, Float> = Pair(t.position.x, t.position.y)
            val size: Pair<Float, Float> = Pair(t.size.x, t.size.y)

            territories[t.id] = Territory(t.id, t.continent, position, size)
        }

        val jsonConnections = boardData.connections
        for(conn in jsonConnections) {
            val territoryOrigin = territories[conn[0]]!!

            for(i in 1 until conn.size) {
                val territoryTarget = territories[conn[i]]!!

                // Don't add duplicate connections
                if(!territoryOrigin.connections.contains(territoryTarget)) {
                    territoryOrigin.connections.add(territoryTarget)
                    territoryTarget.connections.add(territoryOrigin)
                }
            }
        }

        return territories.values.toList()
    }

    @Serializable
    data class BoardData (
        val territories: List<TerritoryData>,
        val connections: List<List<Int>>
    )

    @Serializable
    data class TerritoryData(
        val id: Int,
        val continent: Continent,
        val position: XY,
        val size: XY
    )

    @Serializable
    data class XY(
        val x: Float,
        val y: Float
    )
}