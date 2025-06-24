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
import com.se2gruppe5.risikofrontend.game.GameActivity
import com.se2gruppe5.risikofrontend.lobby.LobbyActivity
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LobbyActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LobbyActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun testBackButtonNavigatesToMenuActivity() {
        onView(withId(R.id.backBtn)).perform(click())
        Intents.intended(hasComponent(MenuActivity::class.java.name))
    }
    @Test
    fun testAllButtonsAreDisplayed() {
        onView(withId(R.id.player1Btn)).check(matches(isDisplayed()))
        //onView(withId(R.id.player2Btn)).check(matches(isDisplayed()))
        //onView(withId(R.id.player3Btn)).check(matches(isDisplayed()))
        //onView(withId(R.id.player4Btn)).check(matches(isDisplayed()))
        //onView(withId(R.id.player5Btn)).check(matches(isDisplayed()))
        //onView(withId(R.id.player6Btn)).check(matches(isDisplayed()))
        onView(withId(R.id.backBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.startGameBtn)).check(matches(isDisplayed()))
    }

    @Test
    fun testAllButtonsAreClickable() {
        onView(withId(R.id.player1Btn)).check(matches(isClickable()))
        onView(withId(R.id.player2Btn)).check(matches(isClickable()))
        onView(withId(R.id.player3Btn)).check(matches(isClickable()))
        onView(withId(R.id.player4Btn)).check(matches(isClickable()))
        onView(withId(R.id.player5Btn)).check(matches(isClickable()))
        onView(withId(R.id.player6Btn)).check(matches(isClickable()))
        onView(withId(R.id.backBtn)).check(matches(isClickable()))
        onView(withId(R.id.startGameBtn)).check(matches(isClickable()))
    }
        @Test
    fun testStartGameButtonNavigatesToGame() {
        onView(withId(R.id.startGameBtn)).perform(click())
        Intents.intended(hasComponent(GameActivity::class.java.name))
    }


}
