package com.se2gruppe5.risikofrontend

import androidx.lifecycle.MutableLiveData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.google.gson.JsonSyntaxException
import com.se2gruppe5.risikofrontend.players.PlayerEventHandler
import com.se2gruppe5.risikofrontend.players.PlayerUpdateListener


class PlayerEventHandler {

    private lateinit var testListener: TestPlayerUpdateListener
    private lateinit var eventHandler: PlayerEventHandler

    class TestPlayerUpdateListener : PlayerUpdateListener {
        var players: List<String>? = null
        override fun onPlayersUpdated(players: List<String>) {
            this.players = players
        }
    }

    @Before
    fun setUp() {
        testListener = TestPlayerUpdateListener()
        eventHandler = PlayerEventHandler(testListener)
    }

    @Test
    fun testOnMessage_nullMessageEvent_doesNothing() {
        eventHandler.onMessage("playerEvent", null)
        assertNull(testListener.players)
    }

    @Test(expected = JsonSyntaxException::class)
    fun testHandlePlayerMessage_invalidJson_throwsException() {
        val json = "{ invalid json"
        // Aufruf über Reflection oder Sichtbarkeit ändern (siehe unten)
        eventHandler.handlePlayerMessage(json)
    }

    @Test
    fun testHandlePlayerMessage_validJson_parsesCorrectly() {
        val json = """["Alice", "Bob"]"""
        eventHandler.handlePlayerMessage(json)
        assertEquals(listOf("Alice", "Bob"), testListener.players)
    }
}

