package com.se2gruppe5.risikofrontend.game.territoryIO

import com.se2gruppe5.risikofrontend.game.dataclasses.Player
import com.se2gruppe5.risikofrontend.game.dataclasses.Territory

class TerritoryManager {

    private val territoryList: MutableList<Territory> = mutableListOf()

    fun addTerritory(territory: Territory) {
        checkTerritoryValid(territory)
        if (territoryList.contains(territory)) {
            throw IllegalArgumentException("Territory (object) already in list.")

        }
        if (territoryList.any { it.id == territory.id }) {
            throw IllegalArgumentException("Territory ID duplicated!?")
        }

        territoryList.add(territory)


    }

    //This should never be needed
    fun removeTerritory(territory: Territory) {
        checkTerritoryValid(territory)
        if (!territoryList.contains(territory)) {
            throw IllegalArgumentException("Territory not in list.")
        }
        territoryList.remove(territory)

    }

    /**
     * let player=null be "no owner"
     */
    fun assignOwner(territory: Territory, player: Player?) {
        checkTerritoryValid(territory)
        if (player != null) {
            checkPlayerValid(player)
            territory.visualization.changeColor(player.color)
        }
        territory.owner = player
    }

    private fun checkTerritoryValid(territory: Territory) {
        if (territory.id <= 0) {
            throw IllegalArgumentException("Territory ID invalid.")
        }
    }

    private fun checkPlayerValid(player: Player) {
        if (player.id <= 0) {
            throw IllegalArgumentException("Player ID invalid.")
        }
    }

}