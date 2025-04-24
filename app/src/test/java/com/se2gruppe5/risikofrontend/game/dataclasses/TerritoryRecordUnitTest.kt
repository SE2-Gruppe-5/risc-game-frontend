package com.se2gruppe5.risikofrontend.game.dataclasses

import org.junit.Test

import org.junit.Assert.*

class TerritoryRecordUnitTest {
    @Test
    fun inst() {
        val p = TerritoryRecord(1,1)
        assertNotNull(p)
    }
}