package com.se2gruppe5.risikofrontend

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.se2gruppe5.risikofrontend.lobby.CreateLobbyActivity
import com.se2gruppe5.risikofrontend.lobby.LobbyActivity
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateLobbyActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(CreateLobbyActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun testUIElementsAreDisplayed() {
        onView(withId(R.id.title)).check(matches(isDisplayed()))
        onView(withId(R.id.yourname)).check(matches(isDisplayed()))
        onView(withId(R.id.name_input)).check(matches(isDisplayed()))
        onView(withId(R.id.createLobbyBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.backBtn)).check(matches(isDisplayed()))
    }
    @Test
    fun testCreateLobbyButtonWorks() {
        onView(withId(R.id.createLobbyBtn)).check(matches(isClickable()))
        onView(withId(R.id.createLobbyBtn)).perform(click())
        Intents.intended(hasComponent(LobbyActivity::class.java.name))
    }

    @Test
    fun testBackButtonWorks() {
        onView(withId(R.id.backBtn)).check(matches(isClickable()))
        onView(withId(R.id.backBtn)).perform(click())
        Intents.intended(hasComponent(MenuActivity::class.java.name))
    }
}

