package com.se2gruppe5.risikofrontend.game.popup

import android.app.AlertDialog
import android.content.Context
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Cheating
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Unfair
import com.se2gruppe5.risikofrontend.game.dice.diceModels.IDice

class RollDiceAlert(context: Context) {

    var registerLambda: () -> Unit = {}
    var deregisterLambda: () -> Unit = {}
    var setCheatLambda: (IDice) -> Unit = {}

    private val popup: AlertDialog = AlertDialog.Builder(context)
        .setTitle("Shake!")
        .setMessage("Shake Phone to Roll Dice!")
        .setCancelable(false)

        //Wire up buttons
        // (this is a very hacky workaround and only allows 3 buttons, sufficient for this project)
        // (Better solution would be custom xml)
        .setNeutralButton("Back") { _, _ ->
            dismissShakePromptDialog()
        }
        .setNegativeButton("Cheat mildly") { _, _ ->
            setCheatLambda(Dice1d6Unfair())
        }
        .setPositiveButton("Cheat blatantly") { _, _ ->
            setCheatLambda(Dice1d6Cheating())
        }

        .create()

    fun showShakePromptDialog() {
        registerLambda()
        popup.show()

        // Must be called after showing popup
        val cancelButton = popup.getButton(AlertDialog.BUTTON_NEUTRAL)
        cancelButton.isEnabled = false
    }

    fun dismissShakePromptDialog() {
        deregisterLambda()
        popup.dismiss()
    }
}
