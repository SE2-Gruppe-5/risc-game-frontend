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
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Field
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class MapPreviewActivityTest {

    // FakeClient, ohne NetworkClient zu erben (da final)
    class FakeClient(private val success: Boolean) {
        suspend fun cheatConquer(gameId: UUID, playerId: UUID, territoryId: Int): Result<Unit> {
            return if (success) Result.success(Unit)
            else Result.failure(Exception("Fake network failure"))
        }
    }

    // 🧪 Testfall: Erfolg
    @Test
    fun testCheatButtonSuccessToastShown() {
        val scenario = ActivityScenario.launch(MapPreviewActivity::class.java)
        scenario.onActivity { activity ->

            // Reflektiere das private Feld "networkClient"
            val field: Field = activity.javaClass.getDeclaredField("networkClient")
            field.isAccessible = true

            // Setze auf FakeClient mit Erfolg
            val proxy = object {
                suspend fun cheatConquer(g: UUID, p: UUID, t: Int) = FakeClient(true).cheatConquer(g, p, t)
            }
            field.set(activity, proxy)

            // Button klicken
            onView(withId(R.id.cheatButton)).perform(click())
        }

        // Prüfe Toast
        onView(withText("Erfolg: Territory übernommen"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    // 🧪 Testfall: Fehler
    @Test
    fun testCheatButtonFailureToastShown() {
        val scenario = ActivityScenario.launch(MapPreviewActivity::class.java)
        scenario.onActivity { activity ->

            // Reflektiere networkClient
            val field: Field = activity.javaClass.getDeclaredField("networkClient")
            field.isAccessible = true

            val proxy = object {
                suspend fun cheatConquer(g: UUID, p: UUID, t: Int) = FakeClient(false).cheatConquer(g, p, t)
            }
            field.set(activity, proxy)

            // Button klicken
            onView(withId(R.id.cheatButton)).perform(click())
        }

        // Prüfe Fehler-Toast
        onView(withText("Fehler: Fake network failure"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    // ⚙️ Hilfsklasse: Matcher für Toasts
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
