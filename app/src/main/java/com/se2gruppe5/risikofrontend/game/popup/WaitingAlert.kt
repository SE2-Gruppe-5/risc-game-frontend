package com.se2gruppe5.risikofrontend.game.popup

import android.app.AlertDialog
import android.content.Context

class WaitingAlert(
    context: Context,
    title: String = "Waiting",
    message: String = "Waiting for other player to roll dice..."
): AlertDialog(context) {

    init {
        setTitle(title)
        setMessage(message)
        setCancelable(false)
    }

    fun update(title: String, message: String) {
        setTitle(title)
        setMessage(message)
    }

}
