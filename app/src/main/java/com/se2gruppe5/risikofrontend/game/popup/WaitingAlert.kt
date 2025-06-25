package com.se2gruppe5.risikofrontend.game.popup

import android.app.AlertDialog
import android.content.Context

class WaitingAlert(context: Context): AlertDialog(context) {
    init {
        setTitle("Waiting")
        setMessage("Waiting for other player to roll dice...")
        setCancelable(false)
    }
}
