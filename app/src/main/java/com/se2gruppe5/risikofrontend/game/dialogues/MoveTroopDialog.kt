package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.network.INetworkClient
import com.se2gruppe5.risikofrontend.network.NetworkClient
import kotlinx.coroutines.runBlocking

class MoveTroopDialog(
    context: Context,
    maxTroops: Int,
    minTroops: Int = 2,
    private val fromTerritory: ITerritoryVisual,
    private val toTerritory: ITerritoryVisual,
) : TroopDialog(context, maxTroops, minTroops) {

    init {
        setTitle("Move Troops from ${fromTerritory.territoryRecord.id} to ${toTerritory.territoryRecord.id}")
    }

    override fun troopAction(troops: Int) {
        fromTerritory.changeStat(fromTerritory.territoryRecord.stat - troops)
        toTerritory.changeStat(toTerritory.territoryRecord.stat + troops)
        runBlocking {
            client.changeTerritory(GameManager.get().getUUID(), fromTerritory.territoryRecord)
            client.changeTerritory(GameManager.get().getUUID(), toTerritory.territoryRecord)
        }

    }
}
