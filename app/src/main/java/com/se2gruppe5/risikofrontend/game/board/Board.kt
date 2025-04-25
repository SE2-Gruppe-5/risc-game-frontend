package com.se2gruppe5.risikofrontend.game.board

import kotlinx.serialization.Serializable


class Board(jsonSrc: String) {
    private val serializer: BoardSerializer = BoardSerializer()
    private val territories: List<Territory> = serializer.loadTerritories(jsonSrc)

    fun getTerritories(): List<Territory> {
        return territories
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

@Serializable
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
}

data class Player(val name: String, val id: Int)
