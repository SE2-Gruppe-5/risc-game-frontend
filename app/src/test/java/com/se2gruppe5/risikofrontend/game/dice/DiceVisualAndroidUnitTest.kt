package com.se2gruppe5.risikofrontend.game.dice

import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dice.dies.IDice
import org.junit.Test


import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.doReturn

import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(Parameterized::class)
class DiceVisualAndroidUnitTest(private val diceRollParam: Int) {
    lateinit var dva: DiceVisualAndroid
    lateinit var mockDice: IDice
    lateinit var mockImageButton: ImageButton
    lateinit var mockTextView: TextView

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(1),
                arrayOf(2),
                arrayOf(6)
            )
        }
    }

    @Before
    fun setup() {
        mockDice = mock<IDice> {
            on { roll() } doReturn diceRollParam
        }
        mockImageButton = mock()
        mockTextView = mock()
        dva = DiceVisualAndroid(mockDice, mockImageButton, mockTextView)

    }

    @Test
    fun testRoll() {
        dva.roll()
        verify(mockDice).roll()
    }

    @Test
    fun testResultUpdate() {
        dva.roll()
        verify(mockTextView).text = diceRollParam.toString()
    }

    @Test
    fun lambdaIsSubscribed() {
        val lambda: (IDiceVisual) -> Unit = mock()
        dva.clickSubscription(lambda)
        dva.imgBTN.performClick()
        verify(lambda).invoke(dva)
    }
}