package com.se2gruppe5.risikofrontend

import android.view.WindowManager
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.se2gruppe5.risikofrontend.devtools.MapPreviewActivity
import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Point2D
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Transform2D
import com.se2gruppe5.risikofrontend.game.enums.Continent
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Size2D
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class MapPreviewActivityTest {


    inner class FakeClient(private val success: Boolean, private val message: String? = null) :
        com.se2gruppe5.risikofrontend.network.INetworkClient {
        override suspend fun sendChat(message: String) {
            // no-op
        }

        override suspend fun createLobby(): String? = "FAKE_LOBBY"

        override suspend fun deleteLobby(lobbyCode: String) {
            // no-op
        }

        override suspend fun joinLobby(lobbyCode: String, playerName: String) {
            // no-op
        }

        override suspend fun leaveLobby(lobbyCode: String) {
            // no-op
        }

        override suspend fun startGame(lobbyCode: String) {
            // no-op
        }

        override suspend fun updatePlayer(gameId: UUID, playerId: UUID, name: String, color: Int) {
            // no-op
        }

        override suspend fun getGameInfo(gameId: UUID) {
            // no-op
        }

        override suspend fun changePhase(gameId: UUID) {
            // no-op
        }

        override suspend fun changeTerritory(gameId: UUID, territory: TerritoryRecord) {
            // no-op
        }

        override suspend fun cardAction(
            gameId: UUID,
            action: String,
            player: PlayerRecord,
            card: CardRecord
        ) {
            // no-op
        }

        override suspend fun cheatConquer(
            gameId: UUID,
            playerId: UUID,
            territoryId: Int
        ): Result<Unit> =
            if (success) Result.success(Unit)
            else Result.failure(Exception(message ?: "Fake network failure"))
    }


    private val fakePlayer = PlayerRecord(
        id = UUID.randomUUID(),
        name = "TestPlayer",
        color = 0xFF0000
    ).apply {
        freeTroops = 5
        capturedTerritory = false
        isCurrentTurn = true
    }

    private val fakeTerritoryRecord = TerritoryRecord(
        id = 1,
        stat = 0,
        continent = Continent.CPU,
        transform = Transform2D(
            position = Point2D(0f, 0f),
            size = Size2D(100f, 100f)
        )
    ).apply {
        owner = fakePlayer.id
    }

    private val fakeTerritory = object : ITerritoryVisual {
        override val territoryRecord: TerritoryRecord = fakeTerritoryRecord
        override fun getTerritoryId(): Int = territoryRecord.id
        override fun setHighlightSelected(selected: Boolean) { /* no-op */
        }

        override fun changeRibbonColor(color: Int) { /* no-op */
        }

        override fun changeBgColor(color: Int) { /* no-op */
        }

        override fun changeStat(stat: Int) { /* no-op */
        }

        override fun changeOwner(owner: UUID?) { /* no-op */
        }

        override fun clickSubscription(lambda: (ITerritoryVisual) -> Unit) { /* no-op */
        }

        override fun getCoordinates(center: Boolean): Point2D = Point2D(0f, 0f)
    }

    /** Setzt den TerritoryManager Singleton mit Fake-Spieler und Fake-Territorium (kann auch null sein) */
    private fun setupTerritoryManagerForTest(
        me: PlayerRecord? = fakePlayer,
        territory: ITerritoryVisual? = fakeTerritory
    ) {
        TerritoryManager.reset()
        TerritoryManager.init(
            me,
            pointingArrow = object : com.se2gruppe5.risikofrontend.game.territory.IPointingArrowUI {
                override fun setColor(color: Int) {
                    // no-op
                }

                override fun setWidth(width: Float) {
                    // no-op
                }

                override fun setCoordinates(from: Point2D, to: Point2D) {}
            },
            toastUtil = object : com.se2gruppe5.risikofrontend.game.managers.IToastUtil {
                override fun showShortToast(message: String) { /* no-op */
                }
            },
            dialogManager = object : com.se2gruppe5.risikofrontend.game.dialogues.IDialogueHandler {
                override fun useAttackDialog(
                    fromTerritory: ITerritoryVisual,
                    toTerritory: ITerritoryVisual,
                    onSuccess: (Int) -> Unit
                ) { /* no-op */
                }
                override fun useTradeCardDialog(
                    player: PlayerRecord, forced: Boolean) {
                    // no-op
                }
                override fun usePlaceTroops(
                    t: ITerritoryVisual,
                    p: PlayerRecord
                ): Boolean {
                    // no-op
                    return false
                }

                override fun useReinforceDialog(
                    fromTerritory: ITerritoryVisual,
                    toTerritory: ITerritoryVisual
                ) { /* no-op */
                }
            }
        )

        // Falls ein TerritoryVisual übergeben wurde, wird es in TerritoryManager eingetragen
        territory?.let {
            val tm = TerritoryManager.get()
            val addTerritoryMethod = TerritoryManager::class.java.getDeclaredMethod(
                "addTerritory",
                ITerritoryVisual::class.java
            )
            addTerritoryMethod.isAccessible = true
            addTerritoryMethod.invoke(tm, it)
        }
    }

    /** Ersetzt den finalen client im TerritoryManager Singleton per Reflection */
    private fun injectFakeClientToTerritoryManager(fakeClient: com.se2gruppe5.risikofrontend.network.INetworkClient) {
        val tmClass = TerritoryManager::class.java
        val clientField = tmClass.getDeclaredField("client")
        clientField.isAccessible = true

        val tmSingletonField = tmClass.getDeclaredField("singleton")
        tmSingletonField.isAccessible = true
        val tmInstance = tmSingletonField.get(null) ?: error("TerritoryManager nicht initialisiert!")

        // Hier nicht versuchen, 'modifiers' zu ändern, einfach setzen:
        clientField.set(tmInstance, fakeClient)
    }

    @Test
    fun testCheatButtonSuccessToastShown() {
        setupTerritoryManagerForTest()
        injectFakeClientToTerritoryManager(FakeClient(success = true))

        val scenario = ActivityScenario.launch(MapPreviewActivity::class.java)
        scenario.onActivity {
        }
            onView(withId(R.id.cheatButton)).perform(click())


        onView(withText("Erfolg: Territory übernommen"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCheatButtonFailureToastShown() {
        setupTerritoryManagerForTest()
        injectFakeClientToTerritoryManager(FakeClient(success = false, message = "Server error"))

        val scenario = ActivityScenario.launch(MapPreviewActivity::class.java)
        scenario.onActivity { }

            onView(withId(R.id.cheatButton)).perform(click())


        onView(withText("Fehler: Server error"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCheatButtonNoPlayerOrTerritoryShowsErrorToast() {
        setupTerritoryManagerForTest(me = null, territory = null)
        injectFakeClientToTerritoryManager(FakeClient(success = true))

        val scenario = ActivityScenario.launch(MapPreviewActivity::class.java)
        scenario.onActivity { }
            onView(withId(R.id.cheatButton)).perform(click())


        onView(withText("Spieler oder Zielterritorium nicht verfügbar!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCheatUsedThisRoundDisablesFurtherCheating() {
        setupTerritoryManagerForTest()
        injectFakeClientToTerritoryManager(FakeClient(success = true))

        val scenario = ActivityScenario.launch(MapPreviewActivity::class.java)
        scenario.onActivity { activity ->
            // cheatUsedThisRound per Reflection auf true setzen
            val cheatField = MapPreviewActivity::class.java.getDeclaredField("cheatUsedThisRound")
            cheatField.isAccessible = true
            cheatField.setBoolean(activity, true)
        }

            onView(withId(R.id.cheatButton)).perform(click())


        onView(withText("Cheat nur einmal pro Runde erlaubt!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCheatButtonIsDisplayedAndClickable() {
        setupTerritoryManagerForTest()
        injectFakeClientToTerritoryManager(FakeClient(success = true))

        val scenario = ActivityScenario.launch(MapPreviewActivity::class.java)

        onView(withId(R.id.cheatButton))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }

    // Hilfsklasse, um Toast-Meldungen in Espresso zu prüfen
    class ToastMatcher : TypeSafeMatcher<Root>() {
        override fun describeTo(description: Description) {
            description.appendText("is toast")
        }

        override fun matchesSafely(root: Root): Boolean {
            val type = root.windowLayoutParams?.get()?.type
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                val windowToken = root.decorView?.windowToken
                val appToken = root.decorView?.applicationWindowToken
                return windowToken === appToken
            }
            return false
        }
    }
}
