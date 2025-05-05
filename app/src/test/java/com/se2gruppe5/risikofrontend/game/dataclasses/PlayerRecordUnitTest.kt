package com.se2gruppe5.risikofrontend.game.dataclasses

import org.junit.Test

import org.junit.Assert.*

class PlayerRecordUnitTest {
    @Test
    fun inst() {
        PlayerRecord(1,"a",0xFFFFFF)
        //Possibly needed in the future:
        //val p = PlayerRecord(1,"a",0xFFFFFF)
        //assertNotNull(p)
    }
}