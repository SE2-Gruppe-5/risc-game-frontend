package com.se2gruppe5.risikofrontend

import androidx.lifecycle.MutableLiveData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.google.gson.JsonSyntaxException
import com.se2gruppe5.risikofrontend.Players.playerEventHandler


class PlayerEventHandler {
    private lateinit var liveData: MutableLiveData<List<String>>
    private lateinit var eventHandler: playerEventHandler


    @Before
    fun setUp() {
        liveData = MutableLiveData()
        eventHandler = playerEventHandler(liveData)
    }


    @Test
    fun testOnMessage_nullMessageEvent_doesNothing() {
        eventHandler.onMessage("playerEvent", null)
        assertNull(liveData.value)
    }


    @Test(expected = JsonSyntaxException::class)
    fun testHandlePlayerMessage_invalidJson_throwsException() {
        val json = "{ invalid json"
        eventHandler.handlePlayerMessage(json)
    }
}

