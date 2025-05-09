package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.kotlin.verifyNoInteractions


class CardHandlerUnitTest {

    @Mock
    private lateinit var player: PlayerRecord
    @Mock
    private lateinit var cards: MutableList<CardRecord>

    @Before
    fun setUp() {
        player.cards = mutableListOf<CardRecord>();
    }

    @Test
    fun testNullPlayer() {
        CardHandler.getCard(null)
        verifyNoInteractions(cards)
    }

    @Test
    fun getCardIncreasesListSize() {
        CardHandler.getCard(player)
        assertEquals( 1, player.cards.size)
    }
    @Test
    fun checkIfRandomWorks() {
        var infantry = 0
        var cavalry = 0
        var artillery = 0
        for(i in (1..500)){
            CardHandler.getCard(player)
            when(player.cards.get(i).type){
                CardType.Infantry -> infantry++
                CardType.Cavalry -> cavalry++
                CardType.Artillery -> artillery++
            }
        }
        assertTrue(infantry >0 && cavalry > 0 && artillery >0)
    }


}