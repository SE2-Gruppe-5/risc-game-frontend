package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.enums.Continent
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

        val t2 = TerritoryRecord(2, 1, Continent.RAM, Pair(30, 70), Pair(50, 220))
        assertEquals(Pair(55, 180), t2.getCenter())
    }

    @Test
    fun checkConnectedTerritories() {
        val t1 = TerritoryRecord(1, 1, Continent.CPU, Pair(100, 100), Pair(100, 100))
        val t2 = TerritoryRecord(2, 1, Continent.RAM, Pair(100, 100), Pair(100, 100))

        t1.connections.add(t2)
        t2.connections.add(t1)

        assertTrue(t1.isConnected(t2))
    }

    @Test
    fun checkDisconnectedTerritories() {
        val t1 = TerritoryRecord(1, 1, Continent.CPU, Pair(100, 100), Pair(100, 100))
        val t2 = TerritoryRecord(2, 1, Continent.RAM, Pair(100, 100), Pair(100, 100))
        val t3 = TerritoryRecord(3, 1, Continent.MMC, Pair(100, 100), Pair(100, 100))

        t1.connections.add(t3)
        t2.connections.add(t3)

        assertFalse(t1.isConnected(t2))
    }
}
