package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import com.se2gruppe5.risikofrontend.game.popup.WaitingAlert
import kotlinx.coroutines.runBlocking

class DefendTroopDialog(
    context: Context,
    maxTroops: Int,
    minTroops: Int = 1,
    fromTerritory: TerritoryRecord,
    atTerritory: TerritoryRecord,
    troopCount: Int
) : TroopDialog(context, maxTroops, minTroops) {

    val from = fromTerritory
    val at = atTerritory

    private val gameManager = GameManager.get()

    init {
        setTitle("You are being attacked at ${atTerritory.id} with $troopCount troops!")
    }

    override fun troopAction(troops: Int) {
        gameManager.requestDiceRolls(troops) {result -> myDiceRolledCallback(result)}
        gameManager.requestOpponentDiceThrow { result -> enemyDiceRolledCallback(result) }
    }

    private var myDiceRolled: Boolean = false
    private var myRoll: List<Int> = ArrayList()
    private var enemyDiceRolled: Boolean = false
    private var enemyRoll: List<Int> = ArrayList()
    private val alert = WaitingAlert(context)

    private fun myDiceRolledCallback(result: List<Int>) {
        myRoll = result
        myDiceRolled = true
        runBlocking {
            client.reportDiceStatus(gameManager.getUUID(), from.owner!!, result)
        }
        applyAllRolls()
    }

    private fun enemyDiceRolledCallback(result: List<Int>) {
        enemyRoll = result
        enemyDiceRolled = true
        applyAllRolls()
    }

    private fun applyAllRolls() {
        if(myDiceRolled && enemyDiceRolled) {
            alert.update(
                "Attack results",
                "Your roll: ${myRoll.joinToString()}" +
                        "\nEnemy roll: ${enemyRoll.joinToString()}"
            )
            alert.show()
            alert.setCancelable(true)
        }
        else if(myDiceRolled) {
            alert.show()
        }
    }
}
