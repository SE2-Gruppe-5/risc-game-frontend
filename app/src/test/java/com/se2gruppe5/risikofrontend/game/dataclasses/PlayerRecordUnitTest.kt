package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType
import org.junit.Test

import org.junit.Assert.*
import org.mockito.kotlin.mock
import java.util.UUID

class PlayerRecordUnitTest {

    private lateinit var card: CardRecord

    @Test
    fun addCard() {
        card = mock()
        var player = PlayerRecord(UUID.randomUUID(), "a", 0xFFFFFF)
        player.cards.add(card)
        assertEquals(1, player.cards.size)

    }

    @Test
    fun addTroops() {
        var player = PlayerRecord(UUID.randomUUID(), "a", 0xFFFFFF)
        player.freeTroops += 10
        assertEquals(10, player.freeTroops)

    }

    @Test
    fun getHashWorks() {
        var uuid = UUID.randomUUID()
        val player = PlayerRecord(uuid, "a", 0xFFFFFF)
        assertEquals(uuid.hashCode(), player.hashCode())
    }

    @Test
    fun testEqualsWorksWithSamePlayer() {
        var player = PlayerRecord(UUID.randomUUID(), "a", 0xFFFFFF)
        assertTrue(player.equals(player))
    }

    @Test
    fun testEqualsFalseWithOtherPlayer() {
        var player = PlayerRecord(UUID.randomUUID(), "a", 0xFFFFFF)
        var other = PlayerRecord(UUID.randomUUID(), "a", 0xFFFFFF)
        assertFalse(player.equals(other))
    }

    @Test
    fun testEqualsFalseWithOtherObject() {
        var player = PlayerRecord(UUID.randomUUID(), "a", 0xFFFFFF)
        var other = CardRecord(CardType.Cavalry)
        assertFalse(player.equals(other))
    }

}
