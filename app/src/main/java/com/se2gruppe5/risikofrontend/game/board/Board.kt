package com.se2gruppe5.risikofrontend.game.board

import org.json.JSONArray
import org.json.JSONObject


class Board(jsonSrc: String) {
    private val territories: List<Territory> = loadTerritories(jsonSrc)

    fun getTerritories(): List<Territory> {
        return territories
    }

    private fun loadTerritories(jsonString: String): List<Territory> {
        val json = JSONObject(jsonString)

        val territories: HashMap<Int, Territory> = HashMap()
        val jsonTerritories: JSONArray = json.getJSONArray("territories")

        for(i in 0 until jsonTerritories.length()) {
            val t: JSONObject = jsonTerritories.getJSONObject(i)

            val id: Int = t.getInt("id")
            val continent: Continent = Continent.get_by_name(t.getString("continent"))!!
            val position: Pair<Float, Float> = xyJsonToFloatPair(t.getJSONObject("position"))
            val size: Pair<Float, Float> = xyJsonToFloatPair(t.getJSONObject("size"))

            territories[id] = Territory(id, continent, position, size)
        }

        val jsonConnections: JSONArray = json.getJSONArray("connections")
        for(i in 0 until jsonConnections.length()) {
            val connection: JSONArray = jsonConnections.getJSONArray(i)
            if (connection.length() != 2) {
                throw IllegalStateException("More than two territories share the same connection.")
            }

            val territoryA = territories[connection[0]]!!
            val territoryB = territories[connection[1]]!!

            territoryA.connections.add(territoryB)
            territoryB.connections.add(territoryA)
        }

        return territories.values.toList()
    }

    private fun xyJsonToFloatPair(obj: JSONObject): Pair<Float, Float> {
        val x = obj.getDouble("x").toFloat()
        val y = obj.getDouble("y").toFloat()
        return Pair(x, y)
    }
}


// Temporary: Data classes will be replaced with proper implementation
data class Territory(
    val id: Int,
    var continent: Continent,
    val position: Pair<Float, Float>,
    val size: Pair<Float, Float>,
) {
    val connections: MutableList<Territory> = ArrayList()
    var owner: Player? = null
    var unitCount: Int = 0
}

enum class Continent {
    POWER_SUPPLY,
    MMC,
    RAM,
    DCON,
    CPU,
    ESSENTIALS,
    SOUTHBRIDGE,
    WIRELESS_MESH,
    EMBEDDED_CONTROLLER,
    CMOS;

    companion object {
        fun get_by_name(name: String): Continent? {
            for(entry in Continent.entries) {
                if(entry.name == name) {
                    return entry
                }
            }
            return null
        }
    }
}

data class Player(val name: String, val id: Int)
