package com.se2gruppe5.risikofrontend.game.managers


import android.app.Activity
import android.graphics.Color
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.cards.CardHandler
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import java.util.UUID


class GameManagerUnitTest {

    private lateinit var activity: Activity
    private lateinit var players: List<PlayerRecord>
    private lateinit var me: PlayerRecord
    private lateinit var turnIndicators: List<TextView>

    @Before
    fun setUp() {

        activity = mock()

        // Create test players
        me = PlayerRecord(UUID.randomUUID(), "Markus", Color.RED)
        players = listOf(
            me,
            PlayerRecord(UUID.randomUUID(), "Leo", Color.BLUE)
        )

        // Mock turn indicators
        turnIndicators = listOf(
            mock<TextView>(),
            mock<TextView>(),
            mock<TextView>(),
            mock<TextView>(),
            mock<TextView>(),
            mock<TextView>()
        )

        // Reset GameManager singleton before each test
        GameManager.unitTestReset()
    }

    @After
    fun tearDown() {
        // Reset GameManager singleton after each test
        GameManager.unitTestReset()
    }

    @Test
    fun testSingletonInit() {
        GameManager.init(players, me)
        val gameManager = GameManager.get()
        assertNotNull(gameManager)
        assertEquals(players, gameManager.players)
        assertEquals(me, gameManager.me)
        assertEquals(players[0], GameManager.getCurrentPlayer())
        assertEquals(0, GameManager.getCurrentPlayerIndex())
        assertEquals(Phases.Reinforce, GameManager.getPhase())
    }

    @Test(expected = IllegalStateException::class)
    fun testThrowsOnGet() {
        GameManager.get()
    }

    @Test
    fun testUnitReset() {
        GameManager.init(players, me)
        GameManager.unitTestReset()
        try {
            GameManager.get()
            fail("Expected IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("GameManager must be .init() first!", e.message)
        }
    }

    @Test
    fun getPlayersReturnsPlayers() {
        assertEquals(players, GameManager.getPlayers())
    }

    @Test
    fun testNextPlayerAndGetCard() {
        // Mock CardHandler
        mockStatic(CardHandler::class.java).use {
            GameManager.init(players, me)
            val currentPlayer = GameManager.getCurrentPlayer()!!
            currentPlayer.capturedTerritory = true

            val (phase, index) = GameManager.get().nextPlayer()
            assertEquals(1, index)
            assertEquals(Phases.Reinforce, phase)
            assertEquals(players[1], GameManager.getCurrentPlayer())
            assertFalse(currentPlayer.capturedTerritory)
            CardHandler.getCard(currentPlayer)
        }
    }

    @Test
    fun testNextPlayerResetsToFirstPlayer() {
        GameManager.init(players, me)
        GameManager.get().nextPlayer() // Move to player 2
        val (phase, index) = GameManager.get().nextPlayer() // Should reset to player 1
        assertEquals(0, index)
        assertEquals(Phases.Reinforce, phase)
        assertEquals(players[0], GameManager.getCurrentPlayer())
    }

    @Test
    fun testNextPhaseTransition() {
        GameManager.init(players, me)
        var temp = GameManager.get().nextPhase()
        assertEquals(Phases.Attack, temp.first)
        assertEquals(0, temp.second)

        temp = GameManager.get().nextPhase()
        assertEquals(Phases.Trade, temp.first)
        assertEquals(0, temp.second)

        temp = GameManager.get().nextPhase()
        assertEquals(Phases.Reinforce,temp.first)
        assertEquals(1, temp.second) // Next player
    }

    @Test
    fun testNextPhaseSetsOtherPlayer() {
        val otherPlayer = players[1]
        GameManager.init(players, otherPlayer)
        val (phase, index) = GameManager.get().nextPhase()
        assertEquals(Phases.OtherPlayer, phase)
        assertEquals(0, index)
    }


    @Test
    fun `setPhase updates phase correctly`() {
        GameManager.init(players, me)
        GameManager.setPhase(Phases.Attack)
        assertEquals(Phases.Attack, GameManager.getPhase())
    }
}