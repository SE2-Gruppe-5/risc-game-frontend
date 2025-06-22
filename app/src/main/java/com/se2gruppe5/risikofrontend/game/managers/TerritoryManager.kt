package com.se2gruppe5.risikofrontend.game.managers

import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dialogues.IDialogueHandler
import com.se2gruppe5.risikofrontend.game.enums.Continent
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.IPointingArrowUI
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.network.INetworkClient
import com.se2gruppe5.risikofrontend.network.NetworkClient
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.math.floor
import kotlin.math.round

const val TERRITORY_NO_OWNER_COLOR: Int = 0x999999

class TerritoryManager private constructor(
    val me: PlayerRecord?, val pointingArrow: IPointingArrowUI, val toastUtil: IToastUtil, val dialogueManager: IDialogueHandler
) {
    companion object {

        private var singleton: TerritoryManager? = null

        fun init(me: PlayerRecord?, pointingArrow: IPointingArrowUI, toastUtil: IToastUtil, dialogManager: IDialogueHandler) {
            if (singleton == null) {
                singleton = TerritoryManager(me, pointingArrow, toastUtil, dialogManager)
            }
        }

        //Throws when null [i.e. .get() before .init()]
        fun get(): TerritoryManager {
            return checkNotNull(singleton) { "TerritoryManager must be .init() first!" }
        }

        fun reset() {
            singleton = null
        }
    }

    private val territoryList: MutableList<ITerritoryVisual> = mutableListOf()
    private var prevSelTerritory: ITerritoryVisual? = null

    /**
     * Unit Test only, do not call
     */
    fun setPrevSelTerritory(t: ITerritoryVisual){
        prevSelTerritory = t
    }

    private fun territoriesSanityCheck(t: ITerritoryVisual) {
        //todo
        return
    }

    private fun territoriesSanityCheck(t: TerritoryRecord) {
        //todo
        return
    }

    private fun territoriesSanityCheck(tList: List<TerritoryRecord>) {
        //todo
        for (t in tList) {
            territoriesSanityCheck(t)
        }
    }

    fun updateTerritories(tList: List<TerritoryRecord>) {
        territoriesSanityCheck(tList)
        for (t in tList) {
            updateTerritory(t)
        }
    }

    fun updateTerritory(t: TerritoryRecord) {
        for (i in territoryList) {
            if (i.getTerritoryId() == t.id) {
                i.changeStat(t.stat)
                i.changeOwner(t.owner)
                if (t.owner != null) {
                    i.changeRibbonColor(GameManager.get().getPlayer(t.owner!!)?.color!!)
                } else {
                    i.changeRibbonColor(TERRITORY_NO_OWNER_COLOR)
                }
                break
            }
        }
    }

    fun addTerritory(t: ITerritoryVisual) {
        territoriesSanityCheck(t)

        require(!(territoryList.contains(t))) { "Territory (object) already in list." }
        require(!(territoryList.any { it.territoryRecord.id == t.territoryRecord.id })) {
            "Territory ID duplicated [!?] how is this even possible" }

        territoryList.add(t)
        addLambdaSubscriptions(t)

    }

    //This should never be needed
    fun removeTerritory(t: ITerritoryVisual) {
        territoriesSanityCheck(t)
        require(territoryList.contains(t)) { "Territory not in list." }
        territoryList.remove(t)
    }

    //fun removeTerritoryById(id: Int){} //todo if needed (but this being needed would indicate a smell

    fun containsTerritory(t: ITerritoryVisual): Boolean {
        territoriesSanityCheck(t)
        return (territoryList.contains(t))
    }

    /**
     * let player:=null semantically mean "no owner"
     */
    fun assignOwner(t: ITerritoryVisual, playerRecord: PlayerRecord?) {
        territoriesSanityCheck(t)
        if (playerRecord != null) {
            t.changeRibbonColor(playerRecord.color)
        } else {
            t.changeRibbonColor(TERRITORY_NO_OWNER_COLOR)
        }
        t.territoryRecord.owner = playerRecord?.id
    }

    private fun addLambdaSubscriptions(t: ITerritoryVisual) {
        territoriesSanityCheck(t)
        t.clickSubscription(::hasBeenClicked) //Observer design pattern
    }

     fun hasBeenClicked(t: ITerritoryVisual) {
        val phase = GameManager.get().getCurrentPhase()
        if (myTurn()) {
            if (prevSelTerritory != t && prevSelTerritory != null
                && t.territoryRecord.isConnected(prevSelTerritory!!.territoryRecord)) {

                prevSelTerritory?.let {
                    pointingArrow.setCoordinates(
                        it.getCoordinates(true), t.getCoordinates(true)
                    )
                }

                if (phase == Phases.Reinforce) {
                    requestReinforce(prevSelTerritory!!, t)
                }
                else if (phase == Phases.Attack) {
                    requestAttack(prevSelTerritory!!, t)
                }
            }else if(prevSelTerritory == t && phase == Phases.Reinforce){
                requestPlace(t)
            }

            updateSelected(t)
            changeTerritoryRequest(t.territoryRecord)
        }
    }

    private fun requestPlace(t: ITerritoryVisual) {
        if (isMe(t.territoryRecord.owner)) {
            if(dialogueManager.usePlaceTroops(t,me!!)){
                runBlocking {
                    client.changeTerritory(GameManager.get().getUUID(), t.territoryRecord)
                }
                }
        }
        else {
            toastUtil.showShortToast("You can only place Troops on your own Territories")
        }
    }

    private fun requestReinforce(fromTerritory: ITerritoryVisual, toTerritory: ITerritoryVisual) {
        if (isMe(fromTerritory.territoryRecord.owner) && isMe(toTerritory.territoryRecord.owner)) {
            dialogueManager.useReinforceDialog(fromTerritory, toTerritory)
        }
        else {
            toastUtil.showShortToast("You can only move between your own territories")
        }
    }

    private fun requestAttack(fromTerritory: ITerritoryVisual, toTerritory: ITerritoryVisual) {
        if (isMe(fromTerritory.territoryRecord.owner) && !isMe(toTerritory.territoryRecord.owner)) {
            dialogueManager.useAttackDialog(fromTerritory, toTerritory) { troops ->
                attackTerritory(toTerritory)
            }
        }
        else {
            toastUtil.showShortToast("You cannot attack your own territories")
        }
    }

    private fun attackTerritory(t: ITerritoryVisual) {
        me!!.capturedTerritory = true

        //TODO we should roll dice here instead of just taking over the territory
        t.territoryRecord.owner = me.id
        runBlocking {
            client.changeTerritory(GameManager.get().getUUID(), t.territoryRecord)
        }
    }

    private fun updateSelected(t: ITerritoryVisual) {
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

    val client: INetworkClient = NetworkClient()

    private fun changeTerritoryRequest(t: TerritoryRecord) {
        runBlocking {
            client.changeTerritory(GameManager.get().getUUID(), t)
        }
    }
    fun getTerritoryById(id: Int): ITerritoryVisual? {
        return territoryList.find { it.getTerritoryId() == id }
    }
    fun getPrevSelTerritory(): ITerritoryVisual? {
        return prevSelTerritory
    }

    private fun getOwnedTerritoriesPerPlayer(record: PlayerRecord): List<TerritoryRecord> {
        var l = mutableListOf<TerritoryRecord>()
        for(t in territoryList){
            if(t.territoryRecord.owner!! == record.id) l.add(t.territoryRecord)
        }

        return l;
    }

    fun calculateNewTroops(p: PlayerRecord): Int{
        var territoryList = getOwnedTerritoriesPerPlayer(p)
        var newTroops = (territoryList.size / 3).toInt()
        if(newTroops<3) newTroops = 3
        newTroops += calculateContinentBonus(territoryList)
        return newTroops

    }

    private fun calculateContinentBonus(records: List<TerritoryRecord>) :Int {
        var bonus = 0
        for (type in Continent.entries){
            var regions = type.regions
            for(t in records){
                if (t.continent == type) regions -=1
            }
            if(regions == 0) bonus+= type.bonus
        }
        return bonus

    }
}
