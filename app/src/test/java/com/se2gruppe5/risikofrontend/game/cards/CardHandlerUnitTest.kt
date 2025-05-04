package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import org.junit.Test
import org.junit.Assert.*
import junit.framework.TestCase.assertSame


class CardHandlerUnitTest {

    private val cardHandler = CardHandler()

    @Test
    fun testCorrectAssignmentOfTroopCount() {
        val expectedResults = mapOf(
            2 to 2,
            3 to 4,
            4 to 7,
            5 to 10,
            6 to 13,
            7 to 17,
            8 to 21,
            9 to 25,
            10 to 30
        )

        for ((starCount, expectedTroops) in expectedResults) {
            val player = PlayerRecord(1, "Test", 0xFFFFFF)
            cardHandler.tradeCards(player, starCount)
            assertSame("Failed at starCount $starCount",expectedTroops, player.freeTroops)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testErrorForLowStarcount() {
        val player = PlayerRecord(1, "Test", 0xFFFFFF)
        cardHandler.tradeCards(player, 1)

    }
    @Test(expected = IllegalArgumentException::class)
    fun testErrorForHighStarcount() {
        val player = PlayerRecord(1, "Test", 0xFFFFFF)
        cardHandler.tradeCards(player, 11)
    }

    @Test
    fun testGetCardIncreasesStarcount() {
        val player = PlayerRecord(1, "Test", 0xFFFFFF)
        repeat(100) {
            val before = player.stars
            cardHandler.getCard(player)
            val after = player.stars
            val added = after - before
            assertTrue("Invalid stars added: $added", added == 1 || added == 2, )
        }
    }

}