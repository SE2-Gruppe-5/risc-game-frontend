package com.se2gruppe5.risikofrontend.game.popup

import android.app.AlertDialog
import android.content.Context

class SimpleAlert(
    val context: Context,
    var title: String = "Waiting",
    var message: String = "Waiting for other player to roll dice...",
    var cancelable: Boolean = false
) {
    private var dialog: AlertDialog = AlertDialog.Builder(context).create()
    private var shown: Boolean = false

    fun show() {
        if(!shown) {
            dialog.show()
            shown = true
        }
    }

    fun hide() {
        dialog.hide()
        shown = false
    }

    fun update(title: String, message: String, cancelable: Boolean) {
        this.title = title
        this.message = message
        this.cancelable = cancelable
        regenerate()
    }

    private fun regenerate() {
        dialog.dismiss()
        dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .create()

        if(shown) {
            dialog.show()
        }
    }

}
