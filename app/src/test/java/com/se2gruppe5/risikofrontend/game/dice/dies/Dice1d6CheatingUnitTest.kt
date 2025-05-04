package com.se2gruppe5.risikofrontend.game.dice.dies

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class Dice1d6CheatingUnitTest {
    lateinit var d: IDice

    @Before
    fun setup() {
        d = Dice1d6Cheating()
    }

    @Test
    fun testCheatingDice() {
        repeat(15) {
            var a = d.roll()
            assertTrue(a >= 1 && a <= 6)
        }
    }
}