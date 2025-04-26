package com.se2gruppe5.risikofrontend.game.territory

import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doThrow
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class) //helpful for automatic mock instantiation
class TerritoryVisualAndroidUnitTest {
    @Mock
    lateinit var recordMock: TerritoryRecord
    @Mock
    lateinit var bgColorRibbonMock: TextView
    @Mock
    lateinit var textContentMock: TextView
    @Mock
    lateinit var imgBTNMock: ImageButton
    @Mock
    lateinit var outlineMock: View

    private lateinit var colorStaticMock: MockedStatic<Color>

    private lateinit var territoryVisualAndroid: TerritoryVisualAndroid

    @Before
    fun setUp() {
        //Create Static Mock of "Color"
        colorStaticMock = Mockito.mockStatic(Color::class.java)
        //necessary, as "Color" Class is often being used in conjunction with the mocked objects

        //Initialize class itself
        territoryVisualAndroid = TerritoryVisualAndroid(recordMock, bgColorRibbonMock, textContentMock, imgBTNMock, outlineMock)

        //colorStaticMock.`when`<Int> { Color.argb(any<Int>(), any<Int>(), any<Int>(), any<Int>()) }.thenReturn(0)

        //Fresh new mocks for all testcases
        Mockito.reset(recordMock)
        Mockito.reset(bgColorRibbonMock)
        Mockito.reset(textContentMock)
        Mockito.reset(imgBTNMock)
        Mockito.reset(outlineMock)
    }

    @After
    fun tearDown() {
        //End static mock (afaik not explicitly necessary, should happen automatically.. but still)
        colorStaticMock.close()
    }

    /*
    [!] This is currently only testing the hardcoded values in the class,
    when doing visual overhauls in the future the Test-Cases will need to be adapted appropriately...
     */
    //Test if setHighlight(true) turn's highlighting on
    @Test
    fun setHighlightTrueTest() {
        territoryVisualAndroid.setHighlightSelected(true)
        verify(outlineMock).setBackgroundColor(territoryVisualAndroid.backgroundHighlightColor)
    }

    //Test if setHighlight(false) turn's highlighting off
    @Test
    fun setHighlightFalseTest() {
        territoryVisualAndroid.setHighlightSelected(false)
        verify(outlineMock).setBackgroundColor(territoryVisualAndroid.backgroundNoHighlightColor)
    }

    //Test if changeColor changes color
    @Test
    fun changeColorTest() {
        val color = "#123456".toColorInt()
        territoryVisualAndroid.changeColor(color)
        verify(bgColorRibbonMock).setBackgroundColor(color)
    }

    //Test if exception is caught and IllegalArgument thrown instead
    @Test(expected = IllegalArgumentException::class)
    fun changeColorThrowTest() {
        doThrow(RuntimeException("fail")).whenever(bgColorRibbonMock).setBackgroundColor(any())
        territoryVisualAndroid.changeColor(0)
        //Note: Color passed here doesn't matter, mocked bgColorRibbon always throws error
    }

    //Test if changeStat changes Stat
    @Test
    fun changeStatTest() {
        val a = 10
        territoryVisualAndroid.changeStat(a)
        verify(recordMock).stat = a
        verify(textContentMock).text = a.toString()
    }

    //Test lower limit of allowed stat
    @Test(expected = IllegalArgumentException::class)
    fun changeStatTestTooLowTest() {
        territoryVisualAndroid.changeStat(0)
    }

    //Test upper limit of allowed stat
    @Test(expected = IllegalArgumentException::class)
    fun changeStatTestTooHighTest() {
        territoryVisualAndroid.changeStat(100)
    }

    @Test
    fun clickSubscriptionAddsTest() {
        // Create an empty reference
        var invokedSubscriptionRef: ITerritoryVisual? = null

        // Create Kotlin-Mockito Helper-Object to capture all onClickListener-Objects getting registered to Android APK OnClickListener
        val argCaptor = argumentCaptor<View.OnClickListener>()

        // Subscribe a lambda function to the territoryVisual, that assigns THE CALLER to invokedSubscriptionRef
        // (changing it from null to territoryVisual, if subscribed correctly)
        territoryVisualAndroid.clickSubscription { inv -> invokedSubscriptionRef = inv }

        // Rather than waiting for a click to occur
        // (which won't happen as the imgBTN is mocked and does not actually do what the real thing would):
        // 1) Retrieve the OnClickListener object that was registered on the mocked ImageButton by capturing it during verification
        // (yes this works by calling verify and passing the captor as a param [?!] :huh:)
        verify(imgBTNMock).setOnClickListener(argCaptor.capture())
        // 2) Manually call click() on said OnClickListener object
        argCaptor.firstValue.onClick(imgBTNMock)
        // As the onClickListener entailing my lambda has now been invoked,
        // invokedSubscriptionRef should now be the reference to the caller
        assertSame(territoryVisualAndroid, invokedSubscriptionRef)
        /*
        But why?
        This is to test not only the lambda being performed, but it being performed with the correct object
        i.e. the "subscription" design pattern actually wiring back to the correct instance that called it.
        Creating an arbitrary function to attach or asserting a primitive value being the same would also work,
        it would however be a less robust test case.
        */
    }

    //Tests whether getCoordinates returns top-left-location correctly
    @Test
    fun getCoordinatesAsFloatNoCenterTest() {
        val imgX: Int = 10;
        val imgY: Int = 20;
        val imgSizeX: Int = 30;
        val imgSizeY: Int = 40;

        //Specify Mock-Object behaviour
        //Simulate by-reference array value overwriting, as it works with the real function
        doAnswer { invocation ->
            val arr = invocation.getArgument(0) as IntArray
            arr[0] = imgX;
            arr[1] = imgY;
            null; //Kotlin-Lambdas automatically return the value of the last expression
            //As it is supposed to be a void function, it must return null
        }.whenever(imgBTNMock).getLocationInWindow(any())

        val (x, y) = territoryVisualAndroid.getCoordinatesAsFloat(false) //without center!
        assertEquals(imgX.toFloat(), x)
        assertEquals(imgY.toFloat(), y)
    }

    //Tests whether getCoordinates returns center-location correctly
    @Test
    fun getCoordinatesAsFloatWithCenterTest() {
        val imgX: Int = 10;
        val imgY: Int = 20;
        val imgSizeX: Int = 30;
        val imgSizeY: Int = 40;

        //Specify Mock-Object behaviour
        //Simulate by-reference array value overwriting, as it works with the real function
        doAnswer { invocation ->
            val arr = invocation.getArgument(0) as IntArray
            arr[0] = imgX;
            arr[1] = imgY;
            null; //Kotlin-Lambdas automatically return the value of the last expression
            //As it is supposed to be a void function, it must return null
        }.whenever(imgBTNMock).getLocationInWindow(any())
        // Simulate returning size
        whenever(imgBTNMock.width).thenReturn(imgSizeX)
        whenever(imgBTNMock.height).thenReturn(imgSizeY)

        val (x, y) = territoryVisualAndroid.getCoordinatesAsFloat(true) //with center!
        assertEquals(imgX.toFloat() + imgSizeX/2, x)
        assertEquals(imgY.toFloat() + imgSizeY/2, y)
    }

}