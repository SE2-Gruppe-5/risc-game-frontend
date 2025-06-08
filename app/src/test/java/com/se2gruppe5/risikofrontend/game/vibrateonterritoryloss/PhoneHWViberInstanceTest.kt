package com.se2gruppe5.risikofrontend.game.vibrateonterritoryloss
import org.junit.Test
import org.junit.Assert.*
class PhoneHWViberInstanceTest {
    @Test
    fun instance_setAndGet() {
        val mockViber = MockPhoneHWViber()
        PhoneHWViberInstance.instance = mockViber
        assertEquals(mockViber, PhoneHWViberInstance.instance)
    }
}
