package com.se2gruppe5.risikofrontend.game.dialogues

import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual

interface IDialogueHandler {


    fun useAttackDialog(from: ITerritoryVisual, to: ITerritoryVisual)

    fun useReinforceDialog(from: ITerritoryVisual, to: ITerritoryVisual)

    fun useTradeCardDialog(player: PlayerRecord, forced: Boolean)
    fun usePlaceTroops(t: ITerritoryVisual, p : PlayerRecord)


}
