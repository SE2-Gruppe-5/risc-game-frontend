package com.se2gruppe5.risikofrontend.game.dataclasses

import org.junit.Test

import org.junit.Assert.*
import org.mockito.kotlin.mock

class PlayerRecordUnitTest {
    @Test
    fun inst() {
        PlayerRecord(1,"a",0xFFFFFF)
        //Possibly needed in the future:
        //val p = PlayerRecord(1,"a",0xFFFFFF)
        //assertNotNull(p)
    }


    private lateinit var card: CardRecord
    @Test
    fun addCard(){
        card = mock()
        var player = PlayerRecord(1, "a",0xFFFFFF)
        player.cards.add(card)
        assertEquals( 1, player.cards.size)

    }
    @Test
    fun addTroops(){
        var player = PlayerRecord(1, "a",0xFFFFFF)
        player.freeTroops += 10
        assertEquals( 10, player.freeTroops)

    }
}