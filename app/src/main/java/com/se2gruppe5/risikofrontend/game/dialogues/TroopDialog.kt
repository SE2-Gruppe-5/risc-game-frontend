package com.se2gruppe5.risikofrontend.game.dialogues

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.se2gruppe5.risikofrontend.databinding.DialogMoveTroopsBinding
import com.se2gruppe5.risikofrontend.network.INetworkClient
import com.se2gruppe5.risikofrontend.network.NetworkClient

abstract class TroopDialog(
    context: Context,
    private val maxTroops: Int,
    private val minTroops: Int
) : AlertDialog(context) {

    private val binding: DialogMoveTroopsBinding = DialogMoveTroopsBinding.inflate(LayoutInflater.from(context))
    val client: INetworkClient = NetworkClient()
    private var dismissable: Boolean = true

    init {
        setView(binding.root)

        binding.troopsInput.hint = "$minTroops - $maxTroops"

        setButton(BUTTON_POSITIVE, "OK", null, null)
        setButton(BUTTON_NEGATIVE, "Cancel", null, null)
    }

    fun setDismissable(value: Boolean) {
        dismissable = value
    }

    override fun onStart() {
        super.onStart()
        getButton(BUTTON_POSITIVE).setOnClickListener {
            handlePositiveButton()
        }
        getButton(BUTTON_NEGATIVE).setOnClickListener {
            if(dismissable) {
                dismiss()
            }
        }
    }

    private fun handlePositiveButton() {
        try {
            val troops = binding.troopsInput.text.toString().toInt()
            if (troops in minTroops..maxTroops) {
                troopAction(troops)
                dismiss()
            } else {
                Toast.makeText(context, "Enter a number between $minTroops and $maxTroops", Toast.LENGTH_SHORT).show()
                if(dismissable) {
                    dismiss()
                }
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Invalid number", Toast.LENGTH_SHORT).show()
            if(dismissable) {
                dismiss()
            }
        }
    }

    protected abstract fun troopAction(troops: Int)
}
