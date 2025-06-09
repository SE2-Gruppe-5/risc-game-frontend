package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.times

class CardHandlerUnitTest {

    private lateinit var player: PlayerRecord
    private lateinit var cardsMock: MutableList<CardRecord>
    private val addedCards = mutableListOf<CardRecord>() // Real list to track added cards

    @Before
    fun setUp() {
        player = mock<PlayerRecord>()
        cardsMock = mock<MutableList<CardRecord>>()
        whenever(player.cards).thenReturn(cardsMock)

        // Reset the tracking list before each test
        addedCards.clear()

        // Stub size to return the size of the tracking list
        whenever(cardsMock.size).then { addedCards.size }

        // Stub add to append to the tracking list and return true
        whenever(cardsMock.add(org.mockito.kotlin.any())).then { invocation ->
            val card = invocation.getArgument<CardRecord>(0)
            addedCards.add(card)
            true
        }

        // Stub get to return the card at the specified index from the tracking list
        whenever(cardsMock.get(org.mockito.kotlin.any())).then { invocation ->
            val index = invocation.getArgument<Int>(0)
            addedCards[index]
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNullPlayer() {
        CardHandler.getCard(null)
    }

    @Test
    fun getCardIncreasesListSize() {
        // Initial size is 0
        assertEquals(0, player.cards.size)

        // Call getCard to add a card
        CardHandler.getCard(player)

        // Size should now be 1
        assertEquals(1, player.cards.size)

        // Verify that add was called once
        verify(cardsMock, times(1)).add(org.mockito.kotlin.any())
    }

    @Test
    fun checkIfRandomWorks() {
        val cardCounts = mutableMapOf(
            CardType.Infantry to 0,
            CardType.Cavalry to 0,
            CardType.Artillery to 0
        )

        // Add 500 cards
        repeat(500) { i ->
            CardHandler.getCard(player)
            val card = player.cards[i] // Get the card at index i
            cardCounts[card.type] = cardCounts[card.type]!! + 1
        }

        // Verify that all card types were added at least once
        assertTrue(cardCounts[CardType.Infantry]!! > 0)
        assertTrue(cardCounts[CardType.Cavalry]!! > 0)
        assertTrue(cardCounts[CardType.Artillery]!! > 0)

        // Verify that add was called 500 times
        verify(cardsMock, times(500)).add(org.mockito.kotlin.any())
    }
}
