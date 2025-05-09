package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.enums.CardType
import org.junit.Test

import org.junit.Assert.*

class CardRecordUnitTest {
    @Test
    fun inst() {
        CardRecord(CardType.Infantry)
        //Possibly needed in the future:
        //val c = CardRecord(CardType.Infantry)
        //assertNotNull(c)
    }
}