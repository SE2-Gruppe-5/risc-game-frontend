package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.board.Continent
import org.junit.Test

import org.junit.Assert.*
import java.util.UUID

class TerritoryRecordUnitTest {
    @Test
    fun inst() {
        val t = TerritoryRecord(1,1, Continent.POWER_SUPPLY, Pair(100, 100), Pair(100, 100))
        t.owner = UUID.randomUUID()
        //Possibly needed in the future:
        //val p = TerritoryRecord(1,1)
        //assertNotNull(p)
    }
}