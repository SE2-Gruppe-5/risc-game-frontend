package com.se2gruppe5.risikofrontend.game.vibrateonterritoryloss
import org.junit.Test
import org.junit.Assert.*
import java.util.UUID

class VibrateClientMessageTest {
    @Test
    fun values_areCorrect() {
        val uuid = UUID.randomUUID()
        val msg = VibrateClientMessage(uuid, 600, 200)
        assertEquals(uuid, msg.playerId)
        assertEquals(600, msg.durationMs)
        assertEquals(200, msg.intensity)
    }
}
