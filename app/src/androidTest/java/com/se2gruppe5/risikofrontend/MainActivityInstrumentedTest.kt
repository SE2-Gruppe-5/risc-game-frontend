package com.se2gruppe5.risikofrontend

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 *  !!! Beware: For running on local machine the Android Emulator must be turned on.
 */

@RunWith(AndroidJUnit4::class) //<-- Android VM Test
class MainActivityInstrumentedTest {

    // Launches and closes activity automatically
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Tests whether the Send-Button visually clears it's text-content after sending
     * (Yields decent coverage for now)
     */
    @Test
    fun testButtonClearsEditText() {
        // Emulating Text being typed in
        onView(withId(R.id.txtMessage)).perform(typeText("hello there"))

        closeSoftKeyboard() //closes virtual keyboard

        // hit "Send"
        onView(withId(R.id.button)).perform(click())

        // Assert whether Text-Field is now empty
        onView(withId(R.id.txtMessage)).check(matches(withText("")))
    }

}