package com.se2gruppe5.risikofrontend.game.managers

import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid

const val TERRITORY_NO_OWNER_COLOR: Int = 0x999999

class TerritoryManager private constructor(val me: PlayerRecord, private val pointingArrow: PointingArrowAndroid) {
    companion object {

        //Intentionally not using non-nullable lateInit var for unit test reset funcitonality
        private var singleton: TerritoryManager? = null

        fun init(me: PlayerRecord, pointingArrow: PointingArrowAndroid) {
            if (singleton==null) {
                singleton = TerritoryManager(me, pointingArrow)
            }
        }

        //Throws when null [i.e. .get() before .init()]
        fun get(): TerritoryManager {
            return checkNotNull(singleton) { "TerritoryManager must be .init() first!" }
        }

        /**
         * Do not call this. It is for unit tests only.
         */
        fun unitTestReset(){
            singleton=null
        }
    }

    private var isInSelectMode: Boolean = false
    private var isInAttackMode: Boolean = false
    private val territoryList: MutableList<ITerritoryVisual> = mutableListOf()
    private var prevSelTerritory: ITerritoryVisual? = null;


    fun enterAttackMode() {
        isInAttackMode = true
    }

    fun exitAttackMode() {
        isInAttackMode = false
    }

    fun enterSelectMode() {
        isInSelectMode = true
    }

    fun exitSelectMode() {
        isInSelectMode = false
    }

    fun clearSelection(){
        prevSelTerritory = null;
    }


    fun addTerritory(t: ITerritoryVisual) {
        checkTerritoryValid(t)
        if (territoryList.contains(t)) {
            throw IllegalArgumentException("Territory (object) already in list.")

        }
        if (territoryList.any { it.territoryRecord.id == t.territoryRecord.id }) { //just in case
            throw IllegalArgumentException("Territory ID duplicated [!?] how is this even possible")
        }

        territoryList.add(t)
        addLambdaSubscriptions(t)

    }

    //This should never be needed
    fun removeTerritory(t: ITerritoryVisual) {
        checkTerritoryValid(t)
        if (!territoryList.contains(t)) {
            throw IllegalArgumentException("Territory not in list.")
        }
        territoryList.remove(t)
    }

    //fun removeTerritoryById(id: Int){} //todo if needed (but this being needed would indicate a smell

    fun containsTerritory(t: ITerritoryVisual): Boolean {
        checkTerritoryValid(t)
        return (territoryList.contains(t))
    }

    /**
     * let player:=null semantically mean "no owner"
     */
    fun assignOwner(t: ITerritoryVisual, playerRecord: PlayerRecord?) {
        checkTerritoryValid(t)
        if (playerRecord != null) {
            checkPlayerValid(playerRecord)
            t.changeColor(playerRecord.color)
        }else{
            t.changeColor(TERRITORY_NO_OWNER_COLOR)
        }
        t.territoryRecord.owner = playerRecord
    }



    private fun addLambdaSubscriptions(t: ITerritoryVisual) {
        checkTerritoryValid(t)
        t.clickSubscription(::hasBeenClicked) //Observer design pattern
    }

    private fun hasBeenClicked(t: ITerritoryVisual) {
        //todo: this is a very basic implementation,
        // as soon as we settled on a GameManager (or something similar that imposes the
        // games core gameplay loop 'phases'); this needs to be overhauled!
        // (also don't allow clicking on self!)
        if (isInSelectMode) {
            prevSelTerritory?.let {
                pointingArrow.setCoordinates(
                    it.getCoordinatesAsFloat(true),
                    t.getCoordinatesAsFloat(true))
            }
            if (isInAttackMode) {
                attackTerritory(t)
                t.changeStat(22)
                prevSelTerritory?.changeStat(11)
            }
            updateSelected(t)
        }
    }

    private fun attackTerritory(t: ITerritoryVisual){
        t.changeColor(me.color)
    }

    private fun updateSelected(t: ITerritoryVisual){
        prevSelTerritory?.setHighlightSelected(false)
        prevSelTerritory = t
        t.setHighlightSelected(true)
    }

    private fun checkTerritoryValid(t: ITerritoryVisual) {
        if (t.territoryRecord.id <= 0) {
            throw IllegalArgumentException("Territory ID invalid.")
        }
    }

    private fun checkPlayerValid(playerRecord: PlayerRecord) {
        if (playerRecord.id <= 0) {
            throw IllegalArgumentException("Player ID invalid.")
        }
    }
}