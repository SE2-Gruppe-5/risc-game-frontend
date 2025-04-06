package com.se2gruppe5.risikofrontend


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.se2gruppe5.risikofrontend.lobby.LobbyActivity
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MenuActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun testCreateLobbyButtonNavigatesToLobbyActivity() {
        onView(withId(R.id.createLobbyBtn)).perform(click())
        Intents.intended(hasComponent(LobbyActivity::class.java.name))
    }
}