package com.se2gruppe5.risikofrontend.game.vibrateonterritoryloss
import org.junit.Test
import org.junit.Assert.*

class MockPhoneHWViberTest {

    @Test
    fun `should not throw when vibrate is called`() {
        val mockViber = MockPhoneHWViber()
        mockViber.vibrate(300, 100)
        assertNotNull(mockViber) //simpler Check
    }
}
