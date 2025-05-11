package com.se2gruppe5.risikofrontend.game.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.se2gruppe5.risikofrontend.databinding.DialogMoveTroopsBinding

abstract class TroopDialog(
    context: Context,
    private val maxTroops: Int,
    private val minTroops: Int
) : AlertDialog(context) {

    private val binding: DialogMoveTroopsBinding = DialogMoveTroopsBinding.inflate(LayoutInflater.from(context))

    init {
        setView(binding.root)

        binding.troopsInput.hint = "$minTroops - $maxTroops"

        setButton(BUTTON_POSITIVE, "OK") { _, _ ->
            handlePositiveButton()
        }
        setButton(BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dismiss()
        }
    }

    private fun handlePositiveButton() {
        try {
            val troops = binding.troopsInput.text.toString().toInt()
            if (troops in minTroops..maxTroops) {
                troopAction(troops)
            } else {
                Toast.makeText(context, "Enter a number between $minTroops and $maxTroops", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid number", Toast.LENGTH_SHORT).show()
        }
    }

    protected abstract fun troopAction(troops: Int)
}