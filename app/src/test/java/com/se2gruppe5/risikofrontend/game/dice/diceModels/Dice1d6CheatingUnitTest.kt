package com.se2gruppe5.risikofrontend.game.dice.diceModels

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class Dice1d6CheatingUnitTest {
    lateinit var d1: IDice
    lateinit var d2: IDice

    @Before
    fun setup() {
        d1 = Dice1d6Cheating()
        d2 = Dice1d6Cheating(Random)
    }

    @Test
    fun testCheatingDice() {
        repeat(15) {
            var a = d1.roll()
            assertTrue(a==6)
        }
    }
    @Test
    fun testCheatingDiceCustomConstructor() {
        repeat(15) {
            var a = d2.roll()
            assertTrue(a==6)
        }
    }
}
