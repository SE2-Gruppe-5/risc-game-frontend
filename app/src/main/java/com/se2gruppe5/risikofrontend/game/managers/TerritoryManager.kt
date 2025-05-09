package com.se2gruppe5.risikofrontend.game.managers

import android.R.bool
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.se2gruppe5.risikofrontend.game.Dialogs.MoveTroopDialog
import com.se2gruppe5.risikofrontend.game.GameActivity
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid

const val TERRITORY_NO_OWNER_COLOR: Int = 0x999999

class TerritoryManager private constructor(val me: PlayerRecord, private val pointingArrow: PointingArrowAndroid, val activity: Activity) {
    companion object {

        //Intentionally not using non-nullable lateInit var for unit test reset funcitonality
        @SuppressLint("StaticFieldLeak")
        private var singleton: TerritoryManager? = null

        fun init(me: PlayerRecord, pointingArrow: PointingArrowAndroid, activity: Activity) {
            if (singleton==null) {
                singleton = TerritoryManager(me, pointingArrow, activity)
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

    private val territoryList: MutableList<ITerritoryVisual> = mutableListOf()
    private var prevSelTerritory: ITerritoryVisual? = null;



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
        if(myTurn()) {
            if (prevSelTerritory != t && prevSelTerritory != null) {
                prevSelTerritory?.let {
                    pointingArrow.setCoordinates(
                        it.getCoordinatesAsFloat(true),
                        t.getCoordinatesAsFloat(true))
                }
                if (GameManager.phase == Phases.Reinforce) {
                    if(prevSelTerritory!!.territoryRecord.owner == me && t.territoryRecord.owner == me) {
                        val dialog = MoveTroopDialog(
                            context = activity,
                            maxTroops = prevSelTerritory!!.territoryRecord.stat - 2,
                            minTroops = 2,
                            fromTerritory = prevSelTerritory!!,
                            toTerritory = t
                        ).show()
                    }else{
                        Toast.makeText(activity, "You can only move between your own territories",
                            Toast.LENGTH_SHORT).show()
                    }
                }

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
    private fun myTurn(): Boolean {
        return me == GameManager.currentPlayer
    }
}