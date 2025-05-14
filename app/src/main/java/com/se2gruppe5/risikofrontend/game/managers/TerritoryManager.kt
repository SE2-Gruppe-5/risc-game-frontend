package com.se2gruppe5.risikofrontend.game.managers

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast

import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dialogues.AttackTroopDialog
import com.se2gruppe5.risikofrontend.game.dialogues.MoveTroopDialog

import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.IPointingArrowUI
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.network.INetworkClient
import com.se2gruppe5.risikofrontend.network.NetworkClient
import kotlinx.coroutines.runBlocking
import java.util.UUID

const val TERRITORY_NO_OWNER_COLOR: Int = 0x999999

class TerritoryManager private constructor(val me: PlayerRecord?, val pointingArrow: IPointingArrowUI, val activity: Activity) {
    companion object {

        @SuppressLint("StaticFieldLeak")
        private var singleton: TerritoryManager? = null

        fun init(me: PlayerRecord?, pointingArrow: IPointingArrowUI, activity: Activity) {
            if (singleton==null) {
                singleton = TerritoryManager(me, pointingArrow, activity)
            }
        }

        //Throws when null [i.e. .get() before .init()]
        fun get(): TerritoryManager {
            return checkNotNull(singleton) { "TerritoryManager must be .init() first!" }
        }

        fun reset(){
            singleton = null
        }
    }

    private val territoryList: MutableList<ITerritoryVisual> = mutableListOf()
    private var prevSelTerritory: ITerritoryVisual? = null

    private fun territoriesSanityCheck(t: ITerritoryVisual){
        //todo
        return
    }
    private fun territoriesSanityCheck(t: TerritoryRecord){
        //todo
        return
    }
    private fun territoriesSanityCheck(tList: List<TerritoryRecord>){
        //todo
        for(t in tList){
            territoriesSanityCheck(t)
        }
    }

    fun updateTerritories(tList: List<TerritoryRecord>){
        territoriesSanityCheck(tList)
        for(t in tList){
            updateTerritory(t)
        }
    }

    fun updateTerritory(t: TerritoryRecord){
        for (i in territoryList){
            if (i.getTerritoryId() == t.id){
                i.changeStat(t.stat)
                i.changeOwner(t.owner)
                if (t.owner != null) {
                    i.changeColor(GameManager.get().getPlayer(t.owner!!)?.color ?: TERRITORY_NO_OWNER_COLOR)
                } else {
                    i.changeColor(TERRITORY_NO_OWNER_COLOR)
                }
                break
            }
        }
    }

    fun addTerritory(t: ITerritoryVisual) {
        territoriesSanityCheck(t)
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
        territoriesSanityCheck(t)
        if (!territoryList.contains(t)) {
            throw IllegalArgumentException("Territory not in list.")
        }
        territoryList.remove(t)
    }

    //fun removeTerritoryById(id: Int){} //todo if needed (but this being needed would indicate a smell

    fun containsTerritory(t: ITerritoryVisual): Boolean {
        territoriesSanityCheck(t)
        return (territoryList.contains(t))
    }

    /**
     * let player:=null semantically mean "no owne r"
     */
    fun assignOwner(t: ITerritoryVisual, playerRecord: PlayerRecord?) {
        territoriesSanityCheck(t)
        if (playerRecord != null) {
            t.changeColor(playerRecord.color)
        }else{
            t.changeColor(TERRITORY_NO_OWNER_COLOR)
        }
        t.territoryRecord.owner = playerRecord?.id
    }

    private fun addLambdaSubscriptions(t: ITerritoryVisual) {
        territoriesSanityCheck(t)
        t.clickSubscription(::hasBeenClicked) //Observer design pattern
    }

    private fun hasBeenClicked(t: ITerritoryVisual) {
        val phase = GameManager.get().getCurrentPhase()
        if(myTurn()) {
            if (prevSelTerritory != t && prevSelTerritory != null) {
                prevSelTerritory?.let {
                    pointingArrow.setCoordinates(
                        it.getCoordinatesAsFloat(true),
                        t.getCoordinatesAsFloat(true))
                }
                if (phase == Phases.Reinforce) {
                    if (isMe(prevSelTerritory!!.territoryRecord.owner) && isMe(t.territoryRecord.owner)) {
                        MoveTroopDialog(
                            context = activity,
                            maxTroops = prevSelTerritory!!.territoryRecord.stat - 1,
                            minTroops = 2,
                            fromTerritory = prevSelTerritory!!,
                            toTerritory = t
                        ).show()
                    }else{
                        Toast.makeText(activity, "You can only move between your own territories",
                            Toast.LENGTH_SHORT).show()
                    }
                }else if(phase == Phases.Attack){
                    if (isMe(prevSelTerritory!!.territoryRecord.owner) && !isMe(t.territoryRecord.owner)) {
                        AttackTroopDialog(
                            context = activity,
                            maxTroops = prevSelTerritory!!.territoryRecord.stat - 1,
                            minTroops = 1,
                            fromTerritory = prevSelTerritory!!,
                            toTerritory = t)
                        { troops ->
                            attackTerritory(t)
                        }.show()
                    }else{
                        Toast.makeText(activity, "You cannot attack your own territories",
                            Toast.LENGTH_SHORT).show()
                    }


                }

            }
            updateSelected(t)
            changeTerritoryRequest(t.territoryRecord)
        }
    }



    private fun attackTerritory(t: ITerritoryVisual){
        me!!.capturedTerritory = true

        //TODO we should roll dice here instead of just taking over the territory
        t.territoryRecord.owner = me.id
        runBlocking {
            client.changeTerritory(GameManager.get().getUUID(), t.territoryRecord)
        }
    }

    private fun updateSelected(t: ITerritoryVisual){
        prevSelTerritory?.setHighlightSelected(false)
        prevSelTerritory = t
        t.setHighlightSelected(true)
    }

    private fun myTurn(): Boolean {
        return GameManager.get().isMyTurn()
    }

    private fun isMe(uuid: UUID?): Boolean {
        return me?.id?.equals(uuid) == true
    }

    val client : INetworkClient = NetworkClient()

    private fun changeTerritoryRequest(t: TerritoryRecord){
        runBlocking {
            client.changeTerritory(GameManager.get().getUUID(), t)
        }
    }
}