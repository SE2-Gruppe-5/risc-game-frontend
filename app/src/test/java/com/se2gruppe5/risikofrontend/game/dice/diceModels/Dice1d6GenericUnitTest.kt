package com.se2gruppe5.risikofrontend.game.dice.diceModels

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

class Dice1d6GenericUnitTest {
    lateinit var d1: IDice
    lateinit var d2: IDice
    lateinit var mockedRandom: Random

    @Before
    fun setup() {
        //Mock Randomization
        mockedRandom = mock()

        d1 = Dice1d6Generic()
        d2 = Dice1d6Generic(mockedRandom)
    }

    @Test
    fun testGenericDice() {
        repeat(15) {
            var a = d1.roll()
            assertTrue(a >= 1 && a <= 6)
        }
    }

    //Test if custom Random() is used
    @Test
    fun testGenericDiceCustomConstructor() {
        whenever(mockedRandom.nextInt(1, 7))
            .thenReturn(-123)

        val result = d2.roll()
        assertEquals(-123, result)

        verify(mockedRandom, times(1)).nextInt(1, 7)
    }
}
