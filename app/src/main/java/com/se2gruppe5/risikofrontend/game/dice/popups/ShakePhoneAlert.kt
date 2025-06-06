package com.se2gruppe5.risikofrontend.game.dice.popups

import android.app.AlertDialog
import android.content.Context
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Cheating
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Unfair
import com.se2gruppe5.risikofrontend.game.dice.diceModels.IDice

class ShakePhoneAlert(context: Context) {

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
        .setPositiveButton("Back") { _, _ ->
            dismissShakePromptDialog()
        }
        .setNegativeButton("Cheat mildly") { _, _ ->
            setCheatLambda(Dice1d6Unfair())
            popup.show() //clicking the button hides it, so show it again
        }
        .setNeutralButton("Cheat blatantly") { _, _ ->
            setCheatLambda(Dice1d6Cheating())
            popup.show() //clicking the button hides it, so show it again
        }

        .create()

    fun showShakePromptDialog() {
        registerLambda()
        popup.show()
    }

    fun dismissShakePromptDialog() {
        deregisterLambda()
        popup.dismiss()
    }
}
