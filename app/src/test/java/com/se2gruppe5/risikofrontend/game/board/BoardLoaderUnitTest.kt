package com.se2gruppe5.risikofrontend.game.board

import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Point2D
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Size2D
import com.se2gruppe5.risikofrontend.game.enums.Continent
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.File


class BoardLoaderUnitTest {
    private lateinit var testBoard: String
    private lateinit var testDuplicateConnections: String

    private lateinit var boardLoader: BoardLoader
    private lateinit var territories: List<TerritoryRecord>

    @Before
    fun setup() {
        testBoard = File("src/test/java/com/se2gruppe5/risikofrontend/game/board/testBoard.json").readText()
        testDuplicateConnections = File("src/test/java/com/se2gruppe5/risikofrontend/game/board/testBoardDuplicateConnections.json").readText()
    }

    @Test
    fun allTerritoriesLoaded() {
        boardLoader = BoardLoader(testBoard)
        territories = boardLoader.territories

        assertEquals(3, territories.size)

        assertTrue(territories.any {it.id == 1})
        assertTrue(territories.any {it.id == 2})
        assertTrue(territories.any {it.id == 3})
    }

    @Test
    fun territoryDetailsCorrect() {
        boardLoader = BoardLoader(testBoard)
        territories = boardLoader.territories

        val territory1: TerritoryRecord = territories[0]
        val territory2: TerritoryRecord = territories[1]
        val territory3: TerritoryRecord = territories[2]

        // Ids correct
        assertEquals(1, territory1.id)
        assertEquals(2, territory2.id)
        assertEquals(3, territory3.id)

        // Positions correct
        assertEquals(Point2D(100f,100f), territory1.transform.position)
        assertEquals(Point2D(200f,100f), territory2.transform.position)
        assertEquals(Point2D(300f,100f), territory3.transform.position)

        // Sizes correct
        assertEquals(Size2D(100f, 100f), territory1.transform.size)
        assertEquals(Size2D(100f, 100f), territory2.transform.size)
        assertEquals(Size2D(100f, 100f), territory3.transform.size)

        // Continent assignment correct
        assertEquals(Continent.RAM, territory1.continent)
        assertEquals(Continent.CPU, territory2.continent)
        assertEquals(Continent.CMOS, territory3.continent)
    }

    @Test
    fun connectionsCorrect() {
        boardLoader = BoardLoader(testBoard)
        territories = boardLoader.territories

        val territory1: TerritoryRecord = territories[0]
        val territory2: TerritoryRecord = territories[1]
        val territory3: TerritoryRecord = territories[2]

        assertTrue(territory1.connections.contains(territory2))
        assertTrue(territory2.connections.contains(territory3))
        assertFalse(territory1.connections.contains(territory3))
        assertFalse(territory3.connections.contains(territory1))
    }

    @Test
    fun connectionsNoDuplicates() {
        boardLoader = BoardLoader(testDuplicateConnections)
        territories = boardLoader.territories

        val territory1: TerritoryRecord = territories[0]
        val territory2: TerritoryRecord = territories[1]

        assertEquals(1, territory1.connections.size)
        assertEquals(1, territory2.connections.size)
    }
}
