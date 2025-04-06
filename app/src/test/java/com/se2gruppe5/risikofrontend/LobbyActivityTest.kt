package com.se2gruppe5.risikofrontend

import android.widget.Button
import com.se2gruppe5.risikofrontend.lobby.LobbyActivity
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class LobbyActivityTest {

    private lateinit var activity: MenuActivity
    @Before
    fun setup() {
        activity = Robolectric.buildActivity(MenuActivity::class.java).create().get()
    }

    @Test
    fun testCreateLobbyButtonNavigation() {
        // Find the button from your XML layout
        val createLobbyBtn = activity.findViewById<Button>(R.id.createLobbyBtn)

        // Simulate button click
        createLobbyBtn.performClick()

        // Get the started intent
        val shadowActivity = shadowOf(activity)
        val startedIntent = shadowActivity.nextStartedActivity

        // Verify intent details
        assertEquals(LobbyActivity::class.java.name, startedIntent.component?.className)
    }





}