package com.se2gruppe5.risikofrontend.game.board

import com.se2gruppe5.risikofrontend.game.enums.Continent
import kotlinx.serialization.Serializable

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
    val x: Int,
    val y: Int
)
