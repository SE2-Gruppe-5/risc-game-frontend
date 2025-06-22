package com.se2gruppe5.risikofrontend.game.cheating

import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Point2D
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Size2D
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Transform2D
import com.se2gruppe5.risikofrontend.game.dialogues.IDialogueHandler
import com.se2gruppe5.risikofrontend.game.enums.Continent
import com.se2gruppe5.risikofrontend.game.managers.IToastUtil
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territory.IPointingArrowUI
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.network.NetworkClient
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.lang.reflect.Field
import java.util.UUID

class CheatConquerTest {
    private val mockClient = mock<OkHttpClient>()
    private val mockCall = mock<Call>()
    private val mockResponse = mock<Response>()


    private lateinit var networkClient: NetworkClient

    private val pointingArrow = mock<IPointingArrowUI>()
    private val toastUtil = mock<IToastUtil>()
    private val dialogueManager = mock<IDialogueHandler>()
    private val playerRecord = mock<PlayerRecord>()


    @Before
    fun setup() {
        networkClient = NetworkClient()

        // Reflectiv ersetzen des internen OkHttpClient-Feldes
        val clientField: Field = NetworkClient::class.java.getDeclaredField("client")
        clientField.isAccessible = true
        clientField.set(networkClient, mockClient)

        // TerritoryManager Singleton neu initialisieren
        TerritoryManager.reset()
        TerritoryManager.init(playerRecord, pointingArrow, toastUtil, dialogueManager)
    }
    @After
    fun teardown() {
        TerritoryManager.reset()
    }


    @Test
    fun `cheatConquer returns success on HTTP 200`() = runBlocking {
        whenever(mockClient.newCall(any())).thenReturn(mockCall)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockResponse.isSuccessful).thenReturn(true)

        val result = networkClient.cheatConquer(UUID.randomUUID(), UUID.randomUUID(), 1)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `cheatConquer returns failure on HTTP error`() = runBlocking {
        whenever(mockClient.newCall(any())).thenReturn(mockCall)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockResponse.isSuccessful).thenReturn(false)
        whenever(mockResponse.code).thenReturn(500)

        val result = networkClient.cheatConquer(UUID.randomUUID(), UUID.randomUUID(), 1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Server error") == true)
    }

    @Test
    fun `cheatConquer returns failure on exception`() = runBlocking {
        whenever(mockClient.newCall(any())).thenThrow(RuntimeException("Connection failed"))

        val result = networkClient.cheatConquer(UUID.randomUUID(), UUID.randomUUID(), 1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Connection failed") == true)
    }
    @Test
    fun `getTerritoryById returns correct territory when found`() {
        val record1 = TerritoryRecord(
            id = 1,
            stat = 0,
            continent = Continent.CPU,
            transform = Transform2D(Point2D(0f, 0f), Size2D(10f, 10f))
        )
        val record2 = TerritoryRecord(
            id = 2,
            stat = 0,
            continent = Continent.CPU,
            transform = Transform2D(Point2D(0f, 0f), Size2D(10f, 10f))
        )
        // Dummy-Implementierung von ITerritoryVisual
        val territory1 = object : ITerritoryVisual {
            override val territoryRecord = record1
            override fun setHighlightSelected(highlighted: Boolean) {}
            override fun changeRibbonColor(color: Int) {}
            override fun changeBgColor(color: Int) {}
            override fun changeStat(stat: Int) {}
            override fun changeOwner(newOwner: UUID?) {}
            override fun clickSubscription(lambda: (ITerritoryVisual) -> Unit) {}
            override fun getCoordinates(center: Boolean): Point2D = Point2D(0f, 0f)
            override fun getTerritoryId(): Int = territoryRecord.id
        }

        val territory2 = object : ITerritoryVisual {
            override val territoryRecord = record2
            override fun setHighlightSelected(highlighted: Boolean) {}
            override fun changeRibbonColor(color: Int) {}
            override fun changeBgColor(color: Int) {}
            override fun changeStat(stat: Int) {}
            override fun changeOwner(newOwner: UUID?) {}
            override fun clickSubscription(lambda: (ITerritoryVisual) -> Unit) {}
            override fun getCoordinates(center: Boolean): Point2D = Point2D(0f, 0f)
            override fun getTerritoryId(): Int = territoryRecord.id
        }

        val manager = TerritoryManager.get()
        manager.addTerritory(territory1)
        manager.addTerritory(territory2)

        val result = manager.getTerritoryById(2)
        assertEquals(territory2, result)
    }

    @Test
    fun `getTerritoryById returns null if territory not found`() {
        val manager = TerritoryManager.get()
        val result = manager.getTerritoryById(99)
        assertNull(result)
    }

    @Test
    fun `getPrevSelTerritory returns null when no previous selection`() {
        val manager = TerritoryManager.get()
        assertNull(manager.getPrevSelTerritory())
    }

    @Test
    fun `getPrevSelTerritory returns correct territory when set`() {
        val territory = mock<ITerritoryVisual>()
        val manager = TerritoryManager.get()
        manager.setPrevSelTerritory(territory)
        assertEquals(territory, manager.getPrevSelTerritory())
    }
}
