package com.se2gruppe5.risikofrontend.game.board

import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import kotlinx.serialization.json.Json


class BoardLoader(jsonSrc: String) {
    private val territories: List<TerritoryRecord> = loadTerritories(jsonSrc)

    fun getTerritories(): List<TerritoryRecord> {
        return territories
    }

    private fun loadTerritories(json: String): List<TerritoryRecord> {
        val boardData = Json.decodeFromString<BoardData>(json)

        val territories: HashMap<Int, TerritoryRecord> = HashMap()
        val jsonTerritories = boardData.territories

        for(t in jsonTerritories) {
            val position: Pair<Int, Int> = Pair(t.position.x, t.position.y)
            val size: Pair<Int, Int> = Pair(t.size.x, t.size.y)

            territories[t.id] = TerritoryRecord(t.id, 0, t.continent, position, size)
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
}
