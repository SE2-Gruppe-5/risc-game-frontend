package com.se2gruppe5.risikofrontend

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.se2gruppe5.risikofrontend.game.vibrateonterritoryloss.RealPhoneHWViber
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RealPhoneHWViberAndroidTest {

    @Test
    fun vibrate_doesNotCrash() {
        // echten Kontext holen
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val viber = RealPhoneHWViber(context)

        // Test: Einfach sicherstellen, dass die Methode ohne Exception läuft
        viber.vibrate(300, 100)
    }
}
