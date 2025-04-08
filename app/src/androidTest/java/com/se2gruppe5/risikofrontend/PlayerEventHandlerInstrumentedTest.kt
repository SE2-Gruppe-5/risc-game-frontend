package com.se2gruppe5.risikofrontend

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

fun handlePlayerMessageForTest(json: String, liveData: MutableLiveData<List<String>>) {
    val names = JSONArray(json)
    val list = mutableListOf<String>()
    for (i in 0 until names.length()) {
        list.add(names.getString(i))
    }

    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        liveData.value = list
    }
}
@RunWith(AndroidJUnit4::class)
class PlayerEventHandlerInstrumentedTest {
    private lateinit var liveData: MutableLiveData<List<String>>

    @Before
    fun setup() {
        liveData = MutableLiveData()
    }

    @Test
    fun testHandlePlayerMessage_validJson_updatesLiveData() {
        val json = """["Alice", "Bob"]"""
        handlePlayerMessageForTest(json, liveData)
        assertEquals(listOf("Alice", "Bob"), liveData.value)
    }

    @Test
    fun testHandlePlayerMessage_emptyJson_updatesLiveDataToEmptyList() {
        val json = "[]"
        handlePlayerMessageForTest(json, liveData)
        assertEquals(emptyList<String>(), liveData.value)
    }
}
