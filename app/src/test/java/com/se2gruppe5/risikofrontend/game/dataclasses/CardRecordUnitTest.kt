package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType
import org.junit.Test

import org.junit.Assert.*

class CardRecordUnitTest {
    @Test
    fun inst() { //todo this is a bit useless atm
        //Possibly needed in the future:
        val c = CardRecord(CardType.Infantry)
        assertNotNull(c)
    }
}
