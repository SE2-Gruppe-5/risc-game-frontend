package com.se2gruppe5.risikofrontend.game.dataclasses

import androidx.core.graphics.toColorInt
import org.junit.Test

import org.junit.Assert.*

class PlayerRecordUnitTest {
    @Test
    fun inst() {
        val p = PlayerRecord(1,"a",0xFFFFFF)
        assertNotNull(p)
    }
}