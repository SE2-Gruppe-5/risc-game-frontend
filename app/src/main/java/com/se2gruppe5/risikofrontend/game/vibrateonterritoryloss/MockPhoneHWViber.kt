package com.se2gruppe5.risikofrontend.game.vibrateonterritoryloss

class MockPhoneHWViber : PhoneHWViber {
    override fun vibrate(durationMs: Long, intensity: Int) {
        println("[Mock] Vibrating $durationMs ms at intensity $intensity")
    }
}
