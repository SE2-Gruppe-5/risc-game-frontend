package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context

import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual

class AttackTroopDialog(
        context: Context,
        maxTroops: Int,
        minTroops: Int = 2,
        fromTerritory: ITerritoryVisual,
        toTerritory: ITerritoryVisual,
        private val attackFun: (Int) -> Unit
    ) : TroopDialog(context, maxTroops, minTroops) {

        init {
            setTitle("Attack territory ${toTerritory.getTerritoryId()} from ${fromTerritory.getTerritoryId()}")
        }

        override fun troopAction(troops: Int) {
            attackFun(troops)
        }
    }
