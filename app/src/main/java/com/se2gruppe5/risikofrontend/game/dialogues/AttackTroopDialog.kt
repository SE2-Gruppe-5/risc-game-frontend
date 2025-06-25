package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import com.se2gruppe5.risikofrontend.game.popup.WaitingAlert

import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import kotlinx.coroutines.runBlocking
import kotlin.math.min

class AttackTroopDialog(
    context: Context,
    maxTroops: Int,
    minTroops: Int = 2,
    fromTerritory: ITerritoryVisual,
    toTerritory: ITerritoryVisual
) : TroopDialog(context, maxTroops, minTroops) {

    val to = toTerritory
    val from = fromTerritory

    private val gameManager = GameManager.get()

    private var myDiceRolled: Boolean = true
    private var myRoll: List<Int> = ArrayList()
    private var enemyDiceRolled: Boolean = true
    private var enemyRoll: List<Int> = ArrayList()

    init {
        setTitle("Attack territory ${toTerritory.getTerritoryId()} from ${fromTerritory.getTerritoryId()}")
    }

    override fun troopAction(troops: Int) {
        this.dismiss()
        gameManager.whoAmI().capturedTerritory = true

        runBlocking {
            client.attackTerritory(gameManager.getUUID(), from.territoryRecord, to.territoryRecord, troops)
        }

        gameManager.requestDiceRolls(troops) {result -> myDiceRolledCallback(result)}
        gameManager.requestOpponentDiceThrow {result -> enemyDiceRolledCallback(result)}
    }

    private fun myDiceRolledCallback(result: List<Int>) {
        myRoll = result
        myDiceRolled = true
        applyAllRolls()
    }

    private fun enemyDiceRolledCallback(result: List<Int>) {
        enemyRoll = result
        enemyDiceRolled = true
        applyAllRolls()
    }

    private val alert = WaitingAlert(context)
    private fun applyAllRolls() {
        if(myDiceRolled && enemyDiceRolled) {
            applyRollsToTerritories(myRoll, enemyRoll)

            alert.update(
                "Attack results",
                "Your roll: ${myRoll.joinToString()}" +
                        "\nEnemy roll: ${enemyRoll.joinToString()}}"
            )
            alert.setCancelable(true)

            runBlocking {
                client.changeTerritory(gameManager.getUUID(), to.territoryRecord)
                client.changeTerritory(gameManager.getUUID(), from.territoryRecord)
            }
        }
        else if(myDiceRolled) {
            alert.show()
        }
    }

    private fun applyRollsToTerritories(myRoll: List<Int>, enemyRoll: List<Int>) {
        val numComparisons = min(myRoll.size, enemyRoll.size)

        var enemyTroopsDead = 0
        var ownTroopsDead = 0

        for(i in 1..numComparisons) {
            if(enemyRoll.last() < myRoll.last()){
                enemyTroopsDead++
            }
            else {
                ownTroopsDead++
            }
        }

        to.territoryRecord.stat -= enemyTroopsDead
        from.territoryRecord.stat -= ownTroopsDead

        if(to.territoryRecord.stat <= 0) {
            to.territoryRecord.owner = gameManager.whoAmI().id
            to.territoryRecord.stat = myRoll.size - ownTroopsDead
        }
    }
}
