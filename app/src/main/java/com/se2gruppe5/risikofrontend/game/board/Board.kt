package com.se2gruppe5.risikofrontend.game.board


class Board {
    private val territoryMap: Map<Int, Territory> = listOf(
        Territory(1, Continent.RAM, Pair(100f, 100f), Pair(100f, 100f)),
        Territory(2, Continent.RAM, Pair(100f, 200f), Pair(100f, 100f)),
        Territory(3, Continent.RAM, Pair(100f, 300f), Pair(100f, 100f)),
        Territory(4, Continent.RAM, Pair(100f, 400f), Pair(100f, 100f)),
        Territory(5, Continent.RAM, Pair(100f, 500f), Pair(100f, 100f)),
        Territory(6, Continent.RAM, Pair(100f, 600f), Pair(100f, 100f))
    ).associateBy { it.id }

    private val connections: List<Pair<Int, Int>> = listOf(
        Pair(1, 2), Pair(2, 3), Pair(3, 4), Pair(4, 5), Pair(5, 6)
    )

    init {
        for(connection in connections) {
            val territoryA: Territory = territoryMap[connection.first]!!
            val territoryB: Territory = territoryMap[connection.second]!!

            territoryA.connections.add(territoryB)
            territoryB.connections.add(territoryA)
        }
    }

    fun getTerritories(): List<Territory> {
        return territoryMap.values.toList()
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
    CMOS
}

data class Player(val name: String, val id: Int)
