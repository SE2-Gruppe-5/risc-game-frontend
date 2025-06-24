package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Point2D
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Size2D
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Transform2D
import com.se2gruppe5.risikofrontend.game.enums.Continent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TerritoryRecordUnitTest {

    @Test
    fun getCenterCorrect() {
        val t1 = TerritoryRecord(
            1,
            1,
            Continent.CPU,
            Transform2D(Point2D(100f, 100f), Size2D(200f, 300f))
        )
        assertEquals(Point2D(200f, 250f), t1.getCenter())

        val t2 =
            TerritoryRecord(2, 1, Continent.RAM, Transform2D(Point2D(30f, 70f), Size2D(50f, 220f)))
        assertEquals(Point2D(55f, 180f), t2.getCenter())
    }

    @Test
    fun checkConnectedTerritories() {
        val t1 = TerritoryRecord(
            1,
            1,
            Continent.CPU,
            Transform2D(Point2D(100f, 100f), Size2D(100f, 100f))
        )
        val t2 = TerritoryRecord(
            2,
            1,
            Continent.RAM,
            Transform2D(Point2D(100f, 100f), Size2D(100f, 100f))
        )

        t1.connections.add(t2)
        t2.connections.add(t1)

        assertTrue(t1.isConnected(t2))
    }

    @Test
    fun checkDisconnectedTerritories() {
        val t1 = TerritoryRecord(
            1,
            1,
            Continent.CPU,
            Transform2D(Point2D(100f, 100f), Size2D(100f, 100f))
        )
        val t2 = TerritoryRecord(
            2,
            1,
            Continent.RAM,
            Transform2D(Point2D(100f, 100f), Size2D(100f, 100f))
        )
        val t3 = TerritoryRecord(
            3,
            1,
            Continent.MMC,
            Transform2D(Point2D(100f, 100f), Size2D(100f, 100f))
        )

        t1.connections.add(t3)
        t2.connections.add(t3)

        assertFalse(t1.isConnected(t2))
    }
}
