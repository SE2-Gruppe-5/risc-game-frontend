package com.se2gruppe5.risikofrontend.game.dialogues

import android.app.Activity
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual

interface IDialogueHandler {


    fun useAttackDialog(from: ITerritoryVisual, to: ITerritoryVisual, attackFun: (Int) -> Unit)

    fun useReinforceDialog(from: ITerritoryVisual, to: ITerritoryVisual)




}
