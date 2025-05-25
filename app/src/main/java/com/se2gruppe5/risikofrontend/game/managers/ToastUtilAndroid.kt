package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.widget.Toast


class ToastUtilAndroid(val activity: Activity): IToastUtil {
    override fun showShortToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
