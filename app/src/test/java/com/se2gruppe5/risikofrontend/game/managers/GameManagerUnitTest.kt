package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.graphics.Color
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.network.INetworkClient
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import org.mockito.Mockito.mock as mockStatic
import java.util.UUID

class GameManagerUnitTest {
    private lateinit var activity: Activity
    private lateinit var networkClient: INetworkClient
    private lateinit var players: HashMap<UUID, PlayerRecord>
    private lateinit var me: PlayerRecord
    private lateinit var other: PlayerRecord
    private lateinit var turnIndicators: List<TextView>
    private lateinit var gameManager: GameManager
    private lateinit var territoryManagerMock: TerritoryManager
    private lateinit var gameUUID: UUID

    @Before
    fun setUp() {
        //Mock Android-specifics
        activity = mockStatic(Activity::class.java)
        networkClient = mock()
        turnIndicators = List(6) { mockStatic(TextView::class.java) }

        //Create player for testing
        me = PlayerRecord(UUID.randomUUID(), "Markus", Color.RED)
        other = PlayerRecord(UUID.randomUUID(), "Leo", Color.BLUE)
        players = HashMap<UUID, PlayerRecord>().apply {
            put(me.id, me)
            put(other.id, other)
        }




        //Reset singletons before each test
        TerritoryManager.reset()
        GameManager.reset()

        //Initialize singletons
        territoryManagerMock = mock()
        gameUUID = UUID.randomUUID()
        GameManager.init(me, gameUUID,territoryManagerMock, networkClient, players)
        gameManager = GameManager.get()
    }

    @After
    fun tearDown() {
        GameManager.reset()
    }

    @Test
    fun testSingletonInit() {
        assertNotNull(gameManager)
        assertEquals(players, gameManager.getPlayers())
        assertEquals(me, gameManager.whoAmI())
        assertEquals(Phases.Reinforce, gameManager.getPhase())
    }

    @Test
    fun testInitSingletonTwiceDoesntWork(){
        assertNotNull(gameManager)
        val other = PlayerRecord(UUID.randomUUID(), "a", 0xFFFFFF)
        GameManager.init(other, gameUUID,territoryManagerMock, networkClient, players)
        assertEquals(me, gameManager.whoAmI())

    }

    @Test(expected = IllegalArgumentException::class)
    fun testGetCurrentPlayerReturnsError(){
        GameManager.reset()
        val newPlayers: HashMap<UUID, PlayerRecord> = mutableMapOf<UUID, PlayerRecord>() as HashMap<UUID, PlayerRecord>
        GameManager.init(me, gameUUID,territoryManagerMock, networkClient, newPlayers)
        GameManager.get().getCurrentPlayer()

    }

    @Test(expected = IllegalStateException::class)
    fun testThrowsOnGetBeforeInit() {
        GameManager.reset()
        GameManager.get()
    }

    @Test
    fun testUnitReset() {
        GameManager.reset()
        try {
            GameManager.get()
            fail("Expected IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("GameManager must be .init() first!", e.message)
        }
    }

    @Test
    fun getPlayersReturnsPlayers() {
        assertEquals(players, gameManager.getPlayers())
    }

    @Test
    fun getCurrentPlayerReturnsCorrectRecord() {
        assertEquals(me, gameManager.getCurrentPlayer())
    }

    @Test
    fun receivePhaseUpdateUpdatesCurrentPhase() {
        gameManager.receivePhaseUpdate(Phases.Attack)
        assertEquals(Phases.Attack, gameManager.getPhase())
        assertEquals(Phases.Attack, gameManager.getCurrentPhase())
    }

    @Test
    fun receivePlayerListUpdateUpdatesPlayersAndCurrent() {
        other.isCurrentTurn = true
        me.isCurrentTurn = false
        val updated = HashMap<UUID, PlayerRecord>().apply {
            put(me.id, me)
            put(other.id, other)
        }
        gameManager.receivePlayerListUpdate(updated)
        assertEquals(updated, gameManager.getPlayers())
        assertEquals(other, gameManager.getCurrentPlayer())
    }

    @Test
    fun receiveTerritoryListUpdateDelegatesToTerritoryManager() {
        val dummyTerritories = listOf(TerritoryRecord(1, 2), TerritoryRecord(3, 4))
        gameManager.receiveTerritoryListUpdate(dummyTerritories)
        verify(territoryManagerMock).updateTerritories(dummyTerritories)
    }

    @Test
    fun nextPhaseInvokesNetworkClientWhenCurrentIsMe() = runBlocking {
        gameManager.nextPhase()
        verify(networkClient, times(1)).changePhase(gameManager.getUUID())
    }

    @Test
    fun nextPhaseDoesNotInvokeNetworkClientWhenNotCurrent() = runBlocking {
        other.isCurrentTurn = true
        me.isCurrentTurn = false
        val updated = HashMap<UUID, PlayerRecord>().apply {
            put(me.id, me)
            put(other.id, other)
        }
        gameManager.receivePlayerListUpdate(updated)

        gameManager.nextPhase()
        verify(networkClient, times(0)).changePhase(gameManager.getUUID())
    }

    @Test
    fun utilityGettersReturnCorrectValues() {
        assertNotNull(gameManager.getUUID())
        assertSame(territoryManagerMock, gameManager.getTerritoryManager())
        assertEquals(me, gameManager.whoAmI())
    }

    @Test
    fun whoAmIreturnsMe(){
        assertEquals(me, gameManager.whoAmI())
    }
    @Test
    fun getUUIDReturnsCorrectUUID(){
        assertEquals(gameUUID, gameManager.getUUID())
    }
    @Test
    fun getTerritoryManagerReturnsTerritoryManager(){
        assertEquals(territoryManagerMock,gameManager.getTerritoryManager())
    }


}
