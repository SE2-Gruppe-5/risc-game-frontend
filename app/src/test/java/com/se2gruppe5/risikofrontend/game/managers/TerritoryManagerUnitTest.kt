package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import com.se2gruppe5.risikofrontend.game.enums.Continent
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dialogues.DialogueHandler
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.network.INetworkClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever
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
    private lateinit var record3: TerritoryRecord
    private lateinit var t1: ITerritoryVisual
    private lateinit var t2: ITerritoryVisual
    private lateinit var toastUtil: IToastUtil
    private lateinit var mockClient: INetworkClient

    private lateinit var dialogueHandler: DialogueHandler

    @Before
    fun setUp() {
        pointingArrow = mock()
        mePlayerRecord = PlayerRecord(UUID.randomUUID(), "TestPlayer", 0xFF00FF)
        toastUtil = mock()
        dialogueHandler = mock()

        // Reset and init singleton
        TerritoryManager.reset()
        TerritoryManager.init(mePlayerRecord, pointingArrow, toastUtil, dialogueHandler)
        manager = TerritoryManager.get()

        // Base territory record and visual
        record = TerritoryRecord(1, 1, Continent.CPU, Pair(100, 100), Pair(100, 100))
        t1 = mock {
            on { territoryRecord } doReturn record
            on { getTerritoryId() } doReturn record.id
            on { changeStat(any()) } doAnswer  { record.stat = it.getArgument(0) }
            on { changeOwner(any())} doAnswer {record.owner = it.getArgument(0)}
        }
        record3 = TerritoryRecord(1, 1, Continent.CPU, Pair(100, 100), Pair(100, 100))
        t2 = mock {
            on { territoryRecord } doReturn record3
            on { getTerritoryId() } doReturn record3.id
            on { changeStat(any()) } doAnswer  { record3.stat = it.getArgument(0) }
            on { changeOwner(any())} doAnswer {record3.owner = it.getArgument(0)}
        }

        mockClient = mock()
        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        newOwner = PlayerRecord(UUID.randomUUID(), "NewTest", 0x123456)
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        playerList.put(newOwner.id,newOwner)
        GameManager.reset()
        GameManager.init(mePlayerRecord,UUID.randomUUID(),manager, mockClient, playerList)
    }

    @Test
    fun clickSubscriptionTest() {
        manager.addTerritory(t1)
        val captor = argumentCaptor<(ITerritoryVisual) -> Unit>()
        verify(t1).clickSubscription(captor.capture())
        val clickDoer = captor.firstValue
        clickDoer(t1)
        verify(t1).setHighlightSelected(true)
    }

    @Test
    fun highlightTest() {
        val record1 = TerritoryRecord(123, 1, Continent.CMOS, Pair(100, 100), Pair(100, 100))
        val record2 = TerritoryRecord(321, 1, Continent.DCON, Pair(100, 100), Pair(100, 100))


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
        TerritoryManager.init(newMe, pointingArrow, toastUtil, dialogueHandler)
        val inst = TerritoryManager.get()
        assertEquals(mePlayerRecord, inst.me)
    }

    @Test
    fun meReferenceSetCorrect() {
        assertEquals(mePlayerRecord, manager.me)
    }

    @Test
    fun addTerritory() {
        manager.addTerritory(t1)
        // Verify subscription lambda added
        verify(t1).clickSubscription(any())
    }

    @Test
    fun addAndRemoveValidTerritoryTest() {
        manager.addTerritory(t1)
        assertTrue(manager.containsTerritory(t1))

        manager.removeTerritory(t1)
        assertFalse(manager.containsTerritory(t1))
    }

    @Test(expected = IllegalArgumentException::class)
    fun addTerritoryDuplicateRefTest() {
        manager.addTerritory(t1)
        manager.addTerritory(t1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addTerritoryDuplicateIDTest() {
        val record2 = TerritoryRecord(1, 5, Continent.CPU, Pair(100, 100), Pair(100, 100))
        val t2 = mock<ITerritoryVisual> {
            on { territoryRecord } doReturn record2
            on { getTerritoryId() } doReturn record2.id
        }
        manager.addTerritory(t1)
        manager.addTerritory(t2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun removeNotAddedTerritoryTest() {
        manager.removeTerritory(t1)
    }

    @Test
    fun reAddTerritoryTest() {
        manager.addTerritory(t1)
        manager.removeTerritory(t1)
        manager.addTerritory(t1)
        assertTrue(manager.containsTerritory(t1))
    }

    @Test
    fun assignNewOwnerTest() {
        val newOwner = PlayerRecord(UUID.randomUUID(), "Owner", 0x123456)
        manager.assignOwner(t1, newOwner)
        verify(t1).changeRibbonColor(newOwner.color)
        assertEquals(newOwner.id, record.owner)
    }

    @Test
    fun removeAssignedOwnerTest() {
        val initialOwner = PlayerRecord(UUID.randomUUID(), "Owner", 0x123456)
        manager.assignOwner(t1, initialOwner)
        manager.assignOwner(t1, null)
        verify(t1).changeRibbonColor(TERRITORY_NO_OWNER_COLOR)
        assertNull(record.owner)
    }

    @Test
    fun updateTerritoryTest() {
        manager.addTerritory(t1)
        record.stat = 5
        record.owner = newOwner.id

        assertNotNull(GameManager.get().getPlayer(t1.territoryRecord.owner!!))
        manager.updateTerritory(record)
        verify(t1).changeStat(5)
        verify(t1).changeOwner(newOwner.id)
        verify(t1).changeRibbonColor(newOwner.color)
    }

    @Test
    fun updateTerritoryNoOwnerTest() {
        manager.addTerritory(t1)
        record.stat = 3
        record.owner = null

        manager.updateTerritory(record)
        verify(t1).changeStat(3)
        verify(t1).changeOwner(null)
        verify(t1).changeRibbonColor(TERRITORY_NO_OWNER_COLOR)
    }

    @Test
    fun updateCallsTerrManagerUpdateTest() {
        manager.addTerritory(t1)
        val record2 = TerritoryRecord(1, 2, Continent.MMC, Pair(100, 100), Pair(100, 100)).apply { owner = null }
        manager.updateTerritories(listOf(record, record2))
        verify(t1, times(2)).changeStat(any())
    }

    @Test
    fun testNoTerritoryUpdateIfNotMyTurn(){
        t1.territoryRecord.connections.add(t2.territoryRecord)
        t2.territoryRecord.connections.add(t1.territoryRecord)
        t1.territoryRecord.owner = mePlayerRecord.id
        t2.territoryRecord.owner = mePlayerRecord.id
        whenever(dialogueHandler.useReinforceDialog(any(), eq(t2))).then {
            t2.changeStat(88)
        }

        assertTrue(GameManager.get().getPhase() == Phases.Reinforce)
        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        newOwner.isCurrentTurn = true
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        playerList.put(newOwner.id,newOwner)
        GameManager.get().receivePlayerListUpdate(playerList)

        manager.setPrevSelTerritory(t1)
        manager.hasBeenClicked(t2)
        assert(t2.territoryRecord.stat == 1) { "t2.territoryRecord.stat should be 1, but was ${t2.territoryRecord.stat}" }

    }
    @Test
    fun testTerritoryUpdateIfMyTurn(){
        t1.territoryRecord.connections.add(t2.territoryRecord)
        t2.territoryRecord.connections.add(t1.territoryRecord)
        t1.territoryRecord.owner = mePlayerRecord.id
        t2.territoryRecord.owner = mePlayerRecord.id
        whenever(dialogueHandler.useReinforceDialog(any(), any())).then {
            t2.changeStat(88)
        }

        assertTrue(GameManager.get().getPhase() == Phases.Reinforce)
        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        mePlayerRecord.isCurrentTurn = true
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        playerList.put(newOwner.id,newOwner)
        GameManager.get().receivePlayerListUpdate(playerList)

        manager.setPrevSelTerritory(t1)
        manager.hasBeenClicked(t2)
        assert(t2.territoryRecord.stat == 88) { "t2.territoryRecord.stat should be 88, but was ${t2.territoryRecord.stat}" }

    }
    @Test
    fun testNoTerritoryUpdateIfNotConnected(){

        t1.territoryRecord.owner = mePlayerRecord.id
        t2.territoryRecord.owner = mePlayerRecord.id
        whenever(dialogueHandler.useReinforceDialog(any(), any())).then {
            t2.changeStat(88)
        }

        assertTrue(GameManager.get().getPhase() == Phases.Reinforce)
        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        mePlayerRecord.isCurrentTurn = true
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        playerList.put(newOwner.id,newOwner)
        GameManager.get().receivePlayerListUpdate(playerList)

        manager.setPrevSelTerritory(t1)
        manager.hasBeenClicked(t2)
        assert(t2.territoryRecord.stat == 1) { "t2.territoryRecord.stat should be 1, but was ${t2.territoryRecord.stat}" }

    }
    @Test
    fun testNoTerritoryUpdateIfSameTerritory(){
        t1.territoryRecord.connections.add(t2.territoryRecord)
        t2.territoryRecord.connections.add(t1.territoryRecord)
        t1.territoryRecord.owner = mePlayerRecord.id
        t2.territoryRecord.owner = mePlayerRecord.id
        whenever(dialogueHandler.useReinforceDialog(any(), any())).then {
            t2.changeStat(88)
        }

        assertTrue(GameManager.get().getPhase() == Phases.Reinforce)
        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        mePlayerRecord.isCurrentTurn = true
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        playerList.put(newOwner.id,newOwner)
        GameManager.get().receivePlayerListUpdate(playerList)

        manager.setPrevSelTerritory(t1)
        manager.hasBeenClicked(t1)
        assert(t2.territoryRecord.stat == 1) { "t2.territoryRecord.stat should be 1, but was ${t2.territoryRecord.stat}" }

    }

    @Test
    fun testAttackMoveCapturesTerritory(){
        t1.territoryRecord.connections.add(t2.territoryRecord)
        t2.territoryRecord.connections.add(t1.territoryRecord)
        t1.territoryRecord.owner = mePlayerRecord.id
        t2.territoryRecord.owner = newOwner.id
        whenever(dialogueHandler.useAttackDialog(any(), any(), any())).then {
            t2.changeOwner(mePlayerRecord.id)
        }

        GameManager.get().setPhase(Phases.Attack)
        assertTrue(GameManager.get().getPhase() == Phases.Attack)
        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        mePlayerRecord.isCurrentTurn = true
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        playerList.put(newOwner.id,newOwner)
        GameManager.get().receivePlayerListUpdate(playerList)

        manager.setPrevSelTerritory(t1)
        manager.hasBeenClicked(t2)
        assertEquals(t2.territoryRecord.owner, mePlayerRecord.id)

    }

    @Test
    fun testReinforceForeignTerritoryFails() {
        t1.territoryRecord.connections.add(t2.territoryRecord)
        t2.territoryRecord.connections.add(t1.territoryRecord)
        t1.territoryRecord.owner = mePlayerRecord.id
        t2.territoryRecord.owner = newOwner.id
        whenever(dialogueHandler.useReinforceDialog(any(), eq(t2))).then {
            t2.changeStat(88)
        }

        assertTrue(GameManager.get().getPhase() == Phases.Reinforce)
        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        mePlayerRecord.isCurrentTurn = true
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        playerList.put(newOwner.id,newOwner)
        GameManager.get().receivePlayerListUpdate(playerList)

        manager.setPrevSelTerritory(t1)
        manager.hasBeenClicked(t2)

        verify(toastUtil).showShortToast(any())
        assertEquals(1, t2.territoryRecord.stat)
    }

    @Test
    fun testAttackOwnTerritoryFails() {
        t1.territoryRecord.connections.add(t2.territoryRecord)
        t2.territoryRecord.connections.add(t1.territoryRecord)
        t1.territoryRecord.owner = mePlayerRecord.id
        t2.territoryRecord.owner = mePlayerRecord.id
        whenever(dialogueHandler.useReinforceDialog(any(), eq(t2))).then {
            t2.changeStat(88)
        }

        GameManager.get().setPhase(Phases.Attack)
        assertTrue(GameManager.get().getPhase() == Phases.Attack)

        val playerList: HashMap<UUID, PlayerRecord> = HashMap()
        mePlayerRecord.isCurrentTurn = true
        playerList.put(mePlayerRecord.id,mePlayerRecord)
        GameManager.get().receivePlayerListUpdate(playerList)

        manager.setPrevSelTerritory(t1)
        manager.hasBeenClicked(t2)

        verify(toastUtil).showShortToast(any())
        assertEquals(1, t2.territoryRecord.stat)
    }


}
