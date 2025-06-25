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

    val gameManager = GameManager.get()

    init {
        setTitle("Attack territory ${toTerritory.getTerritoryId()} from ${fromTerritory.getTerritoryId()}")
    }

    override fun troopAction(troops: Int) {
        gameManager.whoAmI().capturedTerritory = true

        runBlocking {
            client.attackTerritory(gameManager.getUUID(), from.territoryRecord, to.territoryRecord, troops)
        }

        val myRoll: List<Int> = gameManager.requestDiceRolls(troops)

        // Wait for opponent dice roll
        val alert = WaitingAlert(context)
        alert.show()
        val enemyRoll = gameManager.getOpponentDiceThrow().sorted()
        alert.hide()

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
            to.territoryRecord.stat = troops - ownTroopsDead
        }

        runBlocking {
            client.changeTerritory(gameManager.getUUID(), to.territoryRecord)
            client.changeTerritory(gameManager.getUUID(), from.territoryRecord)
        }
    }
}
