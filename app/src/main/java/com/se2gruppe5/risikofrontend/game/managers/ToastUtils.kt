package com.se2gruppe5.risikofrontend.game.managers

import android.content.Context
import android.widget.Toast



object ToastUtils {
    fun showShortToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}