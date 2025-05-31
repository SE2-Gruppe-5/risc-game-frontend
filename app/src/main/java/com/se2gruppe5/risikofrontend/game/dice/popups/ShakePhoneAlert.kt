package com.se2gruppe5.risikofrontend.game.dice.popups

import android.app.AlertDialog
import android.content.Context
import com.se2gruppe5.risikofrontend.game.dice.diceHardware.DiceHardwareAndroid
import com.se2gruppe5.risikofrontend.game.dice.diceHardware.IDiceHardware

class ShakePhoneAlert(private val context: Context, private val diceHW: IDiceHardware) {

    fun showShakePromptDialog() {
        AlertDialog.Builder(context)
            .setTitle("Shake!")
            .setMessage("Shake Phone to Roll Dice!")
            .setCancelable(false)
            .setPositiveButton("Cancel") { dialog, _ ->
                (diceHW as? DiceHardwareAndroid)?.sensorDeRegisterListener()
                dialog.dismiss()
            }
            .show()
    }
}
