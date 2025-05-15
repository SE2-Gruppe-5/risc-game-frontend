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

    @Test
    fun getCenterCorrect() {
        val t1 = TerritoryRecord(1, 1, Continent.CPU, Pair(100, 100), Pair(200, 300))
        assertEquals(Pair(200, 250), t1.getCenter())

        val t2 = TerritoryRecord(1, 1, Continent.RAM, Pair(30, 70), Pair(50, 220))
        assertEquals(Pair(55, 180), t2.getCenter())
    }
}
