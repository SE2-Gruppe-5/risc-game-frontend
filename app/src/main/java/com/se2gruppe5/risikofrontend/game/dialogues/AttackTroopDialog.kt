package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import com.se2gruppe5.risikofrontend.game.managers.GameManager

import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import kotlinx.coroutines.runBlocking

class AttackTroopDialog(
    context: Context,
    maxTroops: Int,
    minTroops: Int = 2,
    fromTerritory: ITerritoryVisual,
    toTerritory: ITerritoryVisual
) : TroopDialog(context, maxTroops, minTroops) {

    val to = toTerritory
    val from = fromTerritory
    init {
        setTitle("Attack territory ${toTerritory.getTerritoryId()} from ${fromTerritory.getTerritoryId()}")
    }

    override fun troopAction(troops: Int) {
        GameManager.get().whoAmI().capturedTerritory = true

        //TODO we should roll dice here instead of just taking over the territory
        to.territoryRecord.owner = GameManager.get().whoAmI().id
        if(troops - to.territoryRecord.stat > 0){
            to.changeStat(troops - to.territoryRecord.stat)
        }else{
            to.changeStat(1)
        }
        from.changeStat(from.territoryRecord.stat - troops)

        runBlocking {
            client.changeTerritory(GameManager.get().getUUID(), to.territoryRecord)
            client.changeTerritory(GameManager.get().getUUID(), from.territoryRecord)
        }
    }
}
