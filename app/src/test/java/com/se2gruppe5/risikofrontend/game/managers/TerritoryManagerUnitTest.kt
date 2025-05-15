package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.network.INetworkClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import java.util.UUID
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
    private lateinit var newOwner: PlayerRecord
    private lateinit var manager: TerritoryManager
    private lateinit var record: TerritoryRecord
    private lateinit var t: ITerritoryVisual
    private lateinit var activity: Activity


    @Before
    fun setUp() {

        pointingArrow = mock()
        mePlayerRecord = PlayerRecord(UUID.randomUUID(), "TestPlayer", 0xFF00FF)
        activity = mock()

        // Reset and init singleton
        TerritoryManager.reset()
        TerritoryManager.init(mePlayerRecord, pointingArrow, activity)
        manager = TerritoryManager.get()

        // Base territory record and visual
        record = TerritoryRecord(1, 1)
        t = mock {
            on { territoryRecord } doReturn record
            on { getTerritoryId() } doReturn record.id
        }

        val mockClient: INetworkClient = mock()
        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        newOwner = PlayerRecord(UUID.randomUUID(), "NewTest", 0x123456)
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        playerList.put(newOwner.id,newOwner)
        GameManager.reset()
        GameManager.init(mePlayerRecord,UUID.randomUUID(),manager, mockClient, playerList)
        TerritoryManager.disableToast()
    }

    @Test
    fun clickSubscriptionTest() {
        manager.addTerritory(t)
        val captor = argumentCaptor<(ITerritoryVisual) -> Unit>()
        verify(t).clickSubscription(captor.capture())
        val clickDoer = captor.firstValue
        clickDoer(t)
        verify(t).setHighlightSelected(true)
    }

    @Test
    fun highlightTest() {
        val record1 = TerritoryRecord(123, 1)
        val record2 = TerritoryRecord(321, 1)


        val territory1 = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record1
            on { getTerritoryId() } doReturn record1.id
        }

        val territory2 = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record2
            on { getTerritoryId() } doReturn record2.id
        }

        manager.addTerritory(territory1)
        manager.addTerritory(territory2)

        val cap1 = argumentCaptor<(ITerritoryVisual) -> Unit>()
        verify(territory1).clickSubscription(cap1.capture())
        val handler1 = cap1.firstValue

        val cap2 = argumentCaptor<(ITerritoryVisual) -> Unit>()
        verify(territory2).clickSubscription(cap2.capture())
        val handler2 = cap2.firstValue

        handler1(territory1)
        handler2(territory2)

        verify(territory1).setHighlightSelected(false)
        verify(territory2).setHighlightSelected(true)
    }

    // TerritoryManager.get() should throw if not initialized
    @Test(expected = IllegalStateException::class)
    fun getBeforeInitThrowsTest() {
        TerritoryManager.reset()
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
        // Re-init should not change 'me' reference
        val newMe = PlayerRecord(UUID.randomUUID(), "Test2", 0x00FF00)
        TerritoryManager.init(newMe, pointingArrow, activity)
        val inst = TerritoryManager.get()
        assertEquals(mePlayerRecord, inst.me)
    }

    @Test
    fun meReferenceSetCorrect() {
        assertEquals(mePlayerRecord, manager.me)
    }

    @Test
    fun addTerritory() {
        manager.addTerritory(t)
        // Verify subscription lambda added
        verify(t).clickSubscription(any())
    }

    @Test
    fun addAndRemoveValidTerritoryTest() {
        manager.addTerritory(t)
        assertTrue(manager.containsTerritory(t))

        manager.removeTerritory(t)
        assertFalse(manager.containsTerritory(t))
    }

    @Test(expected = IllegalArgumentException::class)
    fun addTerritoryDuplicateRefTest() {
        manager.addTerritory(t)
        manager.addTerritory(t)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addTerritoryDuplicateIDTest() {
        val record2 = TerritoryRecord(1, 5)
        val t2 = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record2
            on { getTerritoryId() } doReturn record2.id
        }
        manager.addTerritory(t)
        manager.addTerritory(t2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun removeNotAddedTerritoryTest() {
        manager.removeTerritory(t)
    }

    @Test
    fun reAddTerritoryTest() {
        manager.addTerritory(t)
        manager.removeTerritory(t)
        manager.addTerritory(t)
        assertTrue(manager.containsTerritory(t))
    }

    @Test
    fun assignNewOwnerTest() {
        val newOwner = PlayerRecord(UUID.randomUUID(), "Owner", 0x123456)
        manager.assignOwner(t, newOwner)
        verify(t).changeColor(newOwner.color)
        assertEquals(newOwner.id, record.owner)
    }

    @Test
    fun removeAssignedOwnerTest() {
        val initialOwner = PlayerRecord(UUID.randomUUID(), "Owner", 0x123456)
        manager.assignOwner(t, initialOwner)
        manager.assignOwner(t, null)
        verify(t).changeColor(TERRITORY_NO_OWNER_COLOR)
        assertNull(record.owner)
    }

    @Test
    fun updateTerritoryTest() {
        manager.addTerritory(t)
        record.stat = 5
        record.owner = newOwner.id

        assertNotNull(GameManager.get().getPlayer(t.territoryRecord.owner!!))
        manager.updateTerritory(record)
        verify(t).changeStat(5)
        verify(t).changeOwner(newOwner.id)
        verify(t).changeColor(newOwner.color)
    }

    @Test
    fun updateTerritoryNoOwnerTest() {
        manager.addTerritory(t)
        record.stat = 3
        record.owner = null

        manager.updateTerritory(record)
        verify(t).changeStat(3)
        verify(t).changeOwner(null)
        verify(t).changeColor(TERRITORY_NO_OWNER_COLOR)
    }

    @Test
    fun updateCallsTerrManagerUpdateTest() {
        manager.addTerritory(t)
        val record2 = TerritoryRecord(1, 2).apply { owner = null }
        manager.updateTerritories(listOf(record, record2))
        verify(t, times(2)).changeStat(any())
    }



}
