package com.se2gruppe5.risikofrontend.game.dice.diceModels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

class Dice1d6UnfairUnitTest {
    lateinit var d1: IDice
    lateinit var d2: IDice
    lateinit var mockedRandom: Random

    @Before
    fun setup() {
        //Mock Randomization
        mockedRandom = mock()

        d1 = Dice1d6Unfair()
        d2 = Dice1d6Unfair(mockedRandom)
    }

    @Test
    fun testUnfairDice() {
        repeat(15) {
            var a = d1.roll()
            assertTrue(a >= 1 && a <= 6)
        }
    }

    @Test
    fun testUnfairDiceCustomConstructor() {
        whenever(mockedRandom.nextInt(1, 7))
            .thenReturn(2, 5, 4)

        val result = d2.roll()
        assertEquals(5, result) //Best roll out of {2,4,5} is 5

        verify(mockedRandom, times(3)).nextInt(1, 7)
    }
}
