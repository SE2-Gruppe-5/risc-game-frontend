package com.se2gruppe5.risikofrontend

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.se2gruppe5.risikofrontend.players.PlayerEventHandler
import com.se2gruppe5.risikofrontend.players.PlayerUpdateListener
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.Test
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class PlayerEventHandlerInstrumentedTest {

    private lateinit var liveData: MutableLiveData<List<String>>
    private lateinit var handler: PlayerEventHandler

    // Adapter, der MutableLiveData mit dem Listener verbindet
    class LiveDataPlayerListener(private val liveData: MutableLiveData<List<String>>) : PlayerUpdateListener {
        override fun onPlayersUpdated(players: List<String>) {
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                liveData.value = players
            }
        }
    }

    @Before
    fun setup() {
        liveData = MutableLiveData()
        handler = PlayerEventHandler(LiveDataPlayerListener(liveData))
    }

    @Test
    fun testHandlePlayerMessage_validJson_updatesLiveData() {
        val json = """["Alice", "Bob"]"""
        handler.handlePlayerMessage(json)
        assertEquals(listOf("Alice", "Bob"), liveData.value)
    }

    @Test
    fun testHandlePlayerMessage_emptyJson_updatesLiveDataToEmptyList() {
        val json = "[]"
        handler.handlePlayerMessage(json)
        assertEquals(emptyList<String>(), liveData.value)
    }
}
