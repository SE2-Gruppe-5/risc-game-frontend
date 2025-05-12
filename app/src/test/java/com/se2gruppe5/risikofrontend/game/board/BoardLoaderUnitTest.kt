package com.se2gruppe5.risikofrontend.game.board

import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File


class BoardLoaderUnitTest {
    private lateinit var json: String
    private lateinit var boardLoader: BoardLoader
    private lateinit var territories: List<TerritoryRecord>

    @Before
    fun setup() {
        json = File("src/test/java/com/se2gruppe5/risikofrontend/game/board/testBoard.json").readText()
        boardLoader = BoardLoader(json)
        territories = boardLoader.territories
    }

    @Test
    fun allTerritoriesLoaded() {
        assertEquals(3, territories.size)

        assertTrue(territories.any {it.id == 1})
        assertTrue(territories.any {it.id == 2})
        assertTrue(territories.any {it.id == 3})
    }

    @Test
    fun territoryDetailsCorrect() {
        val territory1: TerritoryRecord = territories[0]
        val territory2: TerritoryRecord = territories[1]
        val territory3: TerritoryRecord = territories[2]

        // Ids correct
        assertEquals(1, territory1.id)
        assertEquals(2, territory2.id)
        assertEquals(3, territory3.id)

        // Positions correct
        assertEquals(Pair(100, 100), territory1.position)
        assertEquals(Pair(200, 100), territory2.position)
        assertEquals(Pair(300, 100), territory3.position)

        // Sizes correct
        assertEquals(Pair(100, 100), territory1.size)
        assertEquals(Pair(100, 100), territory2.size)
        assertEquals(Pair(100, 100), territory3.size)

        // Continent assignment correct
        assertEquals(Continent.RAM, territory1.continent)
        assertEquals(Continent.CPU, territory2.continent)
        assertEquals(Continent.CMOS, territory3.continent)
    }

    @Test
    fun connectionsCorrect() {
        val territory1: TerritoryRecord = territories[0]
        val territory2: TerritoryRecord = territories[1]
        val territory3: TerritoryRecord = territories[2]

        assertTrue(territory1.connections.contains(territory2))
        assertTrue(territory2.connections.contains(territory3))
        assertFalse(territory1.connections.contains(territory3))
        assertFalse(territory3.connections.contains(territory1))
    }
}