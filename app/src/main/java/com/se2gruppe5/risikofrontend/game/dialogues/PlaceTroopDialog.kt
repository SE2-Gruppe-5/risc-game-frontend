package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.se2gruppe5.risikofrontend.databinding.DialogMoveTroopsBinding
import com.se2gruppe5.risikofrontend.databinding.DialogTradeCardsBinding
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import kotlin.collections.set

class PlaceTroopDialog(
context: Context,
player: PlayerRecord,
territory: ITerritoryVisual
) : AlertDialog(context)   {
    private val binding: DialogMoveTroopsBinding = DialogMoveTroopsBinding.inflate(LayoutInflater.from(context))

    var free = 0
    var t = territory
    var p = player
    init {
        setView(binding.root)
        free = player.freeTroops
        setTitle("Select how many Troops you want to play on your territory")
        binding.troopsInput.hint = "You have $free troops"

        setButton(BUTTON_POSITIVE, "OK") { _, _ ->
           handlePositiveButton()
        }
        setButton(BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dismiss()
        }
        setView(binding.root)


    }
    private fun handlePositiveButton() {
        try {
            val troops = binding.troopsInput.text.toString().toInt()
            if (troops <= free) {
                t.changeStat(t.territoryRecord.stat + troops)
                p.freeTroops = p.freeTroops - troops
            } else {
                Toast.makeText(context, "Enter a number between 0 and $free", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid number", Toast.LENGTH_SHORT).show()
        }
    }


}
