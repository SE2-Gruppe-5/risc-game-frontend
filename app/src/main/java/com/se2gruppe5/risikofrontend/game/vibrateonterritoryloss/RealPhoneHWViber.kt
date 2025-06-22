package com.se2gruppe5.risikofrontend.game.vibrateonterritoryloss

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class RealPhoneHWViber(private val context: Context) : PhoneHWViber {
    override fun vibrate(durationMs: Long, intensity: Int) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val safeIntensity = intensity.coerceIn(1, 255)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(durationMs, safeIntensity)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
