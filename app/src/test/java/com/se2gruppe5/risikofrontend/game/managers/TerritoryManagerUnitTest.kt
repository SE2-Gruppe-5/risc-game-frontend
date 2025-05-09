package com.se2gruppe5.risikofrontend.game.managers

import com.se2gruppe5.risikofrontend.game.board.Continent
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import org.junit.*
import org.junit.Assert.*
import org.mockito.kotlin.*


/*
Note:
        val t = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record
        }
This suffices to mock away the visual wrapper, as only the "core" territory data object is needed most of the time.
The wrapper may however be needed, as it plays a role in color changes etc.
 */

class TerritoryManagerTestUnitTest {
    private lateinit var pointingArrow: PointingArrowAndroid
    private lateinit var mePlayerRecord: PlayerRecord
    private lateinit var manager: TerritoryManager
    private lateinit var record: TerritoryRecord
    private lateinit var t: ITerritoryVisual

    @Before
    fun setUp() {
        pointingArrow = mock()
        mePlayerRecord = PlayerRecord(123, "TestPlayer", 0xFF00FF)

        TerritoryManager.unitTestReset()

        TerritoryManager.init(mePlayerRecord, pointingArrow)
        manager = TerritoryManager.get()
        record = TerritoryRecord(1, 1, Continent.CPU, Pair(100f, 100f), Pair(100f, 100f))
        t = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record
        }
    }

    //TerritoryManager-Singleton should throw Error when singleton has not been initialized
    @Test(expected = IllegalStateException::class)
    fun getBeforeInitThrowsTest() {
        TerritoryManager.unitTestReset()
        TerritoryManager.get()
    }

    @Test
    fun singletonIsSameInstanceTest() {

        val inst1 = TerritoryManager.get()
        val inst2 = TerritoryManager.get()
        assertSame(inst1, inst2)
    }

    @Test
    fun singletonNotMutableTest() {

        val newMe = PlayerRecord(999, "Test2", 0x00FF00)
        TerritoryManager.init(newMe, pointingArrow) //<-- shouldn't do it
        val inst1 = TerritoryManager.get()
        assertEquals(mePlayerRecord, inst1.me)
    }

    @Test
    fun meReferenceSetCorrect() {

        assertEquals(mePlayerRecord, TerritoryManager.get().me)
    }

    //Try adding a valid territory
    @Test
    fun addTerritory() {

        manager.addTerritory(t)
        // Make sure a lambda subscription has been performed for the territory
        verify(t).clickSubscription(any())

    }

    //Try adding and subsequently removing a valid territory
    @Test
    fun addAndRemoveValidTerritoryTest() {
        manager.addTerritory(t)
        //Has been added?
        assertTrue(manager.containsTerritory(t))

        //Let's remove it and see if it's no longer there
        manager.removeTerritory(t)
        assertFalse(manager.containsTerritory(t))

    }

    //Try adding an invalid territory
    @Test(expected = IllegalArgumentException::class)
    fun addTerritoryInvalidTest() {
        TerritoryManager.init(mePlayerRecord, pointingArrow)
        val invalidRecord = TerritoryRecord(-1,1, Continent.CPU, Pair(100f, 100f), Pair(100f, 100f))
        val invalidT = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn invalidRecord
        }
        manager.addTerritory(invalidT)
    }

    //Try adding the same territory twice (by reference)
    @Test(expected = IllegalArgumentException::class)
    fun addTerritoryDuplicateRefTest() {

        manager.addTerritory(t)
        //This should throw an error now ...
        manager.addTerritory(t)
    }

    //Try adding the same territory twice (by id)
    @Test(expected = IllegalArgumentException::class)
    fun addTerritoryDuplicateIDTest() {

        val record1 = TerritoryRecord(1, 5, Continent.CPU, Pair(100f, 100f), Pair(100f, 100f))
        val record2 = TerritoryRecord(1, 10, Continent.CPU, Pair(100f, 100f), Pair(100f, 100f))
        val t1 = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record1
        }
        val t2 = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record2
        }
        manager.addTerritory(t1)
        //This should throw an error now ...
        manager.addTerritory(t2)
    }

    //Removing a territory that has not been added should throw an error
    @Test(expected = IllegalArgumentException::class)
    fun removeNotAddedTerritoryTest() {

        //This should throw an error
        manager.removeTerritory(t)
    }

    //Removing and adding territories should be allowed
    @Test
    fun reAddTerritoryTest() {

        manager.addTerritory(t)
        assertTrue(manager.containsTerritory(t))
        manager.removeTerritory(t)
        manager.addTerritory(t)
        assertTrue(manager.containsTerritory(t))
    }

    //"Overtake territory"
    @Test
    fun assignNewOwnerTest() {

        val newOwner = PlayerRecord(1, "Test", 0x123456)
        manager.assignOwner(t, newOwner)
        //Color change called?
        verify(t).changeColor(newOwner.color)
        //Owner assigned?
        assertEquals(newOwner, record.owner)
    }

    // Setting the owner to null (i.e. "territory belongs to no-one")
    // Color should be changed to ownerless-color
    @Test
    fun removeAssignedOwnerTest() {

        val initialOwner = PlayerRecord(1, "Test", 0x123456)

        //Assign an owner
        manager.assignOwner(t, initialOwner)
        manager.assignOwner(t, null)
        //No-owner color set called?
        verify(t).changeColor(TERRITORY_NO_OWNER_COLOR)
        //No owner assigned?
        assertNull(record.owner)
    }

    @Test
    fun attackInteractionTest() {
        //Prepare Territories (and mock dependencies)
        val record1 = TerritoryRecord(1, 1, Continent.CPU, Pair(100f, 100f), Pair(100f, 100f))
        val record2 = TerritoryRecord(2, 2, Continent.CPU, Pair(100f, 100f), Pair(100f, 100f))
        val t1 = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record1
            on { getCoordinatesAsFloat(true) } doReturn Pair(5f, 6f)
        }
        val t2 = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record2
            on { getCoordinatesAsFloat(true) } doReturn Pair(10f, 11f)
        }

        //Prepare TerritoryManager
        manager.enterSelectMode()
        manager.enterAttackMode()

        //Capture incoming subscriptions to APK onClick;
        // ... in simple words: The TerritoryManager want's to have hasBeenClicked() called,
        // with the territory sending out the event as a param
        // (cf. lambda-subscription comments in Dice and Territory Impl.)
        val argCaptorA = argumentCaptor<(ITerritoryVisual) -> Unit>()
        val argCaptorB = argumentCaptor<(ITerritoryVisual) -> Unit>()
        manager.addTerritory(t1)
        verify(t1).clickSubscription(argCaptorA.capture())
        manager.addTerritory(t2)
        verify(t2).clickSubscription(argCaptorB.capture())
        val capturedFunctionInvokeA = argCaptorA.firstValue
        val capturedFunctionInvokeB = argCaptorB.firstValue
        //Note: This implicitly also tests whether the correct (subscribed) territory has been notified

        // Select t1
        capturedFunctionInvokeA(t1) //Calls manager's "::hasBeenClicked", i.e. what actually happens
        // Verify all expected selection behaviour
        verify(t1).changeColor(mePlayerRecord.color)
        verify(t1).changeStat(22)
        verify(t1).setHighlightSelected(true)

        // "Attack" t2
        capturedFunctionInvokeB(t2) //Calls manager's ::hasBeenClicked", i.e. what actually happens

        //Verify all click interaction shenanigans
        verify(pointingArrow).setCoordinates(Pair(5f, 6f), Pair(10f, 11f))
        verify(t2).changeColor(mePlayerRecord.color)
        verify(t2).changeStat(22)
        verify(t1).changeStat(11)
        verify(t1).setHighlightSelected(false)
        verify(t2).setHighlightSelected(true)
    }
}
