package com.se2gruppe5.risikofrontend.game.dialogs

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import android.view.LayoutInflater
import com.se2gruppe5.risikofrontend.databinding.DialogMoveTroopsBinding
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual

class AttackTroopDialog(
    context: Context,
    private val maxTroops: Int,
    private val minTroops: Int = 2,
    private val fromTerritory: ITerritoryVisual,
    private val toTerritory: ITerritoryVisual,
    private val attackFun: (Int) -> Unit
) : AlertDialog(context) {

    private val binding: DialogMoveTroopsBinding = DialogMoveTroopsBinding.inflate(LayoutInflater.from(context))

    init {
        setTitle("Attack territory ${toTerritory.getTerritoryId()} from ${fromTerritory.getTerritoryId()}")
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
                attackFun(troops)
            } else {
                Toast.makeText(context, "Enter a number between $minTroops and $maxTroops", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid number", Toast.LENGTH_SHORT).show()
        }
    }
}