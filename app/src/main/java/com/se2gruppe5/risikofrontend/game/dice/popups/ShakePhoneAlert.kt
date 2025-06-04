package com.se2gruppe5.risikofrontend.game.dice.popups

import android.app.AlertDialog
import android.content.Context
import com.se2gruppe5.risikofrontend.game.dice.diceHardware.IDiceHardware

class ShakePhoneAlert(context: Context, private val diceHW: IDiceHardware) {

    private var popup: AlertDialog = AlertDialog.Builder(context)
        .setTitle("Shake!")
        .setMessage("Shake Phone to Roll Dice!")
        .setCancelable(false)
        .setPositiveButton("Back") { _, _ ->
            dismissShakePromptDialog()
        }
        .create()

    fun showShakePromptDialog() {
        popup.show()
    }

    fun dismissShakePromptDialog() {
        diceHW.sensorDeRegisterListener()
        popup.dismiss()
    }
}
