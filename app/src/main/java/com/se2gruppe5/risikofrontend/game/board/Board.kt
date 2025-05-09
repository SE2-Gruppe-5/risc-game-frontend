package com.se2gruppe5.risikofrontend.game.board

import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord


class Board(jsonSrc: String) {
    private val serializer: BoardSerializer = BoardSerializer()
    private val territories: List<TerritoryRecord> = serializer.loadTerritories(jsonSrc)

    fun getTerritories(): List<TerritoryRecord> {
        return territories
    }
}
