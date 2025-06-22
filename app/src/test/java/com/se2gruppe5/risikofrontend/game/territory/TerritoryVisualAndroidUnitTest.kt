package com.se2gruppe5.risikofrontend.game.territory

import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.enums.Continent
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(Parameterized::class)
class TerritoryVisualAndroidUnitTest(
    private val statPTestData: Int,
    private val colPTestData: Int,
    private val posAndSizePTestData: Pair<Pair<Int, Int>, Pair<Int, Int>>
) {
    private lateinit var recordMock: TerritoryRecord

    @Mock
    private lateinit var bgColorRibbonMock: TextView

    @Mock
    private lateinit var textContentMock: TextView

    @Mock
    private lateinit var imgBTNMock: ImageButton

    @Mock
    private lateinit var bgMock: View

    @Mock
    private lateinit var outlineMock: View

    private lateinit var colorStaticMock: MockedStatic<Color>

    private lateinit var territoryVisualAndroid: TerritoryVisualAndroid

    private lateinit var mockitoAnnotationClosable: AutoCloseable

    @Before
    fun setUp() {
        //Same as @RunWith(MockitoJUnitRunner::class), but the @RunWith is taken up by ParameterizedTest
        mockitoAnnotationClosable = MockitoAnnotations.openMocks(this)

        //Create Static Mock of "Color"
        colorStaticMock = Mockito.mockStatic(Color::class.java)
        //necessary, as "Color" Class is often being used in conjunction with the mocked objects

        //colorStaticMock.`when`<Int> { Color.argb(any<Int>(), any<Int>(), any<Int>(), any<Int>()) }.thenReturn(0)

        recordMock = mock{
            on { continent } doReturn Continent.CPU
        }

        //Initialize class itself
        territoryVisualAndroid = TerritoryVisualAndroid(
            recordMock,
            bgColorRibbonMock,
            textContentMock,
            imgBTNMock,
            bgMock,
            outlineMock
        )


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
        mockitoAnnotationClosable.close()
    }


    //Parameterized Test Data
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(1, 0x000000, Pair(Pair(0, 0), Pair(1, 1))),
                arrayOf(24, 0x000000, Pair(Pair(74, 24), Pair(40, 23))),
                arrayOf(62, 0xABCABC, Pair(Pair(87, 1), Pair(30, 31))),
                arrayOf(62, 0x123456, Pair(Pair(12, 95), Pair(29, 41))),
                arrayOf(77, 0xA1B2C3, Pair(Pair(63, 76), Pair(12, 32))),
                arrayOf(99, 0xFFFFFF, Pair(Pair(100, 100), Pair(100, 100)))
            )
        }
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
    fun changeRibbonColorTest() {
        territoryVisualAndroid.changeRibbonColor(colPTestData)
        verify(bgColorRibbonMock).setBackgroundColor(colPTestData)
    }

    @Test
    fun changeBgColorTest() {
        territoryVisualAndroid.changeBgColor(colPTestData)
        verify(imgBTNMock).setBackgroundColor(colPTestData)
    }

    @Test
    fun changeStatTest() {
        territoryVisualAndroid.changeStat(statPTestData)
        verify(recordMock).stat = statPTestData
        verify(textContentMock).text = statPTestData.toString()
    }

    @Test(expected = IllegalArgumentException::class)
    fun changeStatTestTooLowTest() {
        territoryVisualAndroid.changeStat(0)
    }

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
        //posAndSizeTestData is Pair containing two Elements:
        // First Element = Pair containing x and y location int
        // Second Element = Pair containing x and y size
        val imgX: Int = posAndSizePTestData.first.first;
        val imgY: Int = posAndSizePTestData.first.second;

        //Specify Mock-Object behaviour
        //Simulate by-reference array value overwriting, as it works with the real function
        doAnswer { invocation ->
            val arr = invocation.getArgument(0) as IntArray
            arr[0] = imgX;
            arr[1] = imgY;
            null; //Kotlin-Lambdas automatically return the value of the last expression
            //As it is supposed to be a void function, it must return null
        }.whenever(imgBTNMock).getLocationInWindow(any())

        val (x, y) = territoryVisualAndroid.getCoordinates(false) //without center!
        assertEquals(imgX.toFloat(), x)
        assertEquals(imgY.toFloat(), y)
    }

    //Tests whether getCoordinates returns center-location correctly
    @Test
    fun getCoordinatesAsFloatWithCenterTest() {
        //posAndSizeTestData is Pair containing two Elements:
        // First Element = Pair containing x and y location int
        // Second Element = Pair containing x and y size
        val imgX: Int = posAndSizePTestData.first.first;
        val imgY: Int = posAndSizePTestData.first.second;
        val imgSizeX: Int = posAndSizePTestData.second.first;
        val imgSizeY: Int = posAndSizePTestData.second.second;

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

        val (x, y) = territoryVisualAndroid.getCoordinates(true) //with center!
        assertEquals(imgX.toFloat() + imgSizeX / 2, x)
        assertEquals(imgY.toFloat() + imgSizeY / 2, y)
    }

}
