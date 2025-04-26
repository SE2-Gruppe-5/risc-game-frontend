package com.se2gruppe5.risikofrontend.game.dice

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dice.dies.IDice
import junit.framework.TestCase.assertSame
import org.junit.Test


import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn

import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(Parameterized::class)
class DiceVisualAndroidUnitTest(private val diceRollParam: Int) {
    lateinit var diceVisualAndroid: DiceVisualAndroid
    lateinit var diceMock: IDice
    lateinit var imgBTNMock: ImageButton
    lateinit var txtViewMock: TextView

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
        diceMock = mock<IDice> {
            on { roll() } doReturn diceRollParam
        }
        imgBTNMock = mock()
        txtViewMock = mock()
        diceVisualAndroid = DiceVisualAndroid(diceMock, imgBTNMock, txtViewMock)

    }

    @Test
    fun testRoll() {
        diceVisualAndroid.roll()
        verify(diceMock).roll()
    }

    @Test
    fun testResultUpdate() {
        diceVisualAndroid.roll()
        verify(txtViewMock).text = diceRollParam.toString()
    }

    @Test
    fun clickSubscriptionAddsTest() {
        // Create an empty reference
        var invokedSubscriptionRef: IDiceVisual? = null

        // Create Kotlin-Mockito Helper-Object to capture all onClickListener-Objects getting registered to Android APK OnClickListener
        val argCaptor = argumentCaptor<View.OnClickListener>()

        // Subscribe a lambda function to the dva, that assigns THE CALLER to invokedSubscriptionRef
        // (changing it from null to dva, if subscribed correctly)
        diceVisualAndroid.clickSubscription { inv -> invokedSubscriptionRef = inv }

        // Rather than waiting for a click to occur
        // (which won't happen as the imgBTN is mocked and does not actually do what the real thing would):
        // 1) Retrieve the OnClickListener object that was registered on the mocked ImageButton by capturing it during verification
        // (yes this works by calling verify and passing the captor as a param [?!] :huh:)
        verify(imgBTNMock).setOnClickListener(argCaptor.capture())
        // 2) Manually call click() on said OnClickListener object
        argCaptor.firstValue.onClick(imgBTNMock)
        // As the onClickListener entailing my lambda has now been invoked,
        // invokedSubscriptionRef should now be the reference to the caller
        assertSame(diceVisualAndroid, invokedSubscriptionRef)
        /*
        But why?
        This is to test not only the lambda being performed, but it being performed with the correct object
        i.e. the "subscription" design pattern actually wiring back to the correct instance that called it.
        Creating an arbitrary function to attach or asserting a primitive value being the same would also work,
        it would however be a less robust test case.
        */
    }
}