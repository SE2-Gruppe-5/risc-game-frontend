package com.se2gruppe5.risikofrontend.game.dialogues

import android.app.Activity
import android.util.Log
import android.widget.TextView
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import java.io.Console

class DialogueHandler (val activity: Activity) : IDialogueHandler {
    override fun useAttackDialog(
        from: ITerritoryVisual,
        to: ITerritoryVisual,
    ) {
        AttackTroopDialog(
            context = activity,
            maxTroops = from.territoryRecord.stat - 1,
            minTroops = 1,
            fromTerritory = from,
            toTerritory = to,
            )
        .show()
    }

    override fun useReinforceDialog(
        from: ITerritoryVisual,
        to: ITerritoryVisual,
    ) {
        MoveTroopDialog(
            context = activity,
            maxTroops = from.territoryRecord.stat - 1,
            minTroops = 2,
            fromTerritory = from,
            toTerritory = to
        ).show()
    }

    override fun useTradeCardDialog(
        player: PlayerRecord,
        forced: Boolean
    ) {
        TradeCardDialog(
            context = activity,
            player = player,
            mustTrade = forced
        ).show()
        activity.findViewById<TextView>(R.id.freeTroopTxt).text = player.freeTroops.toString()
    }

    override fun usePlaceTroops(t: ITerritoryVisual, p : PlayerRecord){
        PlaceTroopDialog(
            context = activity,
            territory = t,
            player = p
        ).show()

    }


}
