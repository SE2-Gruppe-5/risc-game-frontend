package com.se2gruppe5.risikofrontend.game.Dialogs

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import android.view.LayoutInflater
import com.se2gruppe5.risikofrontend.databinding.DialogMoveTroopsBinding
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual

class MoveTroopDialog(
    context: Context,
    private val maxTroops: Int,
    private val minTroops: Int = 2,
    private val fromTerritory: ITerritoryVisual,
    private val toTerritory: ITerritoryVisual,
) : AlertDialog(context) {

    private val binding: DialogMoveTroopsBinding = DialogMoveTroopsBinding.inflate(LayoutInflater.from(context))

    init {
        setTitle("Move Troops from ${fromTerritory.territoryRecord.id} to ${toTerritory.territoryRecord.id}")
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
                onTroopsSelected(troops)
            } else {
                Toast.makeText(context, "Enter a number between $minTroops and $maxTroops", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onTroopsSelected(troops: Int) {
        fromTerritory.changeStat(fromTerritory.territoryRecord.stat - troops)
        toTerritory.changeStat(toTerritory.territoryRecord.stat + troops)

    }
}