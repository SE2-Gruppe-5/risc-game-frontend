package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import kotlinx.coroutines.runBlocking

class DefendTroopDialog(
    context: Context,
    maxTroops: Int,
    minTroops: Int = 2,
    fromTerritory: TerritoryRecord,
    atTerritory: TerritoryRecord,
    troopCount: Int
) : TroopDialog(context, maxTroops, minTroops) {

    val from = fromTerritory
    val at = atTerritory

    val gameManager = GameManager.get()

    init {
        setTitle("You are being attacked at ${atTerritory.id} with $troopCount troops!")
    }

    override fun troopAction(troops: Int) {
        val diceRolls: List<Int> = gameManager.requestDiceRolls(troops)
        runBlocking {
            client.reportDiceStatus(from.owner!!, diceRolls)
        }
    }
}
