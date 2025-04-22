package com.se2gruppe5.risikofrontend.game.territoryManagement

import android.graphics.Color
import com.se2gruppe5.risikofrontend.game.dataclasses.Player
import com.se2gruppe5.risikofrontend.game.dataclasses.Territory
import com.se2gruppe5.risikofrontend.game.territoryIO.ITerritoryUIWrapper

class TerritoryManager(val me: Player) {

    private var isInSelectMode: Boolean = false
    private var isInAttackMode: Boolean = false
    private val territoryList: MutableList<ITerritoryUIWrapper> = mutableListOf()

    fun enterAttackMode(){
        isInAttackMode = true
    }
    fun exitAttackMode(){
        isInAttackMode = false
    }

    fun enterSelectMode() {
        isInSelectMode = true
    }

    fun exitSelectMode() {
        isInSelectMode = false
    }

    fun addTerritory(t: ITerritoryUIWrapper) {
        checkTerritoryValid(t)
        if (territoryList.contains(t)) {
            throw IllegalArgumentException("Territory (object) already in list.")

        }
        if (territoryList.any { it.territory.id == t.territory.id }) {
            throw IllegalArgumentException("Territory ID duplicated!?")
        }

        territoryList.add(t)
        addLambdaSubscription(t)

    }


    //This should never be needed
    fun removeTerritory(t: ITerritoryUIWrapper) {
        checkTerritoryValid(t)
        if (!territoryList.contains(t)) {
            throw IllegalArgumentException("Territory not in list.")
        }
        territoryList.remove(t)

    }

    /**
     * let player=null be "no owner"
     */
    fun assignOwner(t: ITerritoryUIWrapper, player: Player?) {
        checkTerritoryValid(t)
        if (player != null) {
            checkPlayerValid(player)
            t.changeColor(player.color)
        }
        t.territory.owner = player
    }

    private fun addLambdaSubscription(t: ITerritoryUIWrapper) {
        checkTerritoryValid(t)
        t.subscribeLambda(::hasBeenClicked)
    }

    private fun hasBeenClicked(t: ITerritoryUIWrapper) {
        if (isInSelectMode) {
            if (isInAttackMode) {
                t.changeColor(me.color)
            }
        }
    }

    private fun checkTerritoryValid(t: ITerritoryUIWrapper) {
        if (t.territory.id <= 0) {
            throw IllegalArgumentException("Territory ID invalid.")
        }
    }

    private fun checkPlayerValid(player: Player) {
        if (player.id <= 0) {
            throw IllegalArgumentException("Player ID invalid.")
        }
    }

}