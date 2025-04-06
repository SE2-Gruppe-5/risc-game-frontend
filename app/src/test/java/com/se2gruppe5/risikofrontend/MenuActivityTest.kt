package com.se2gruppe5.risikofrontend

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Button
import androidx.test.core.app.ApplicationProvider
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = [28])
class MenuActivityTest {

    private lateinit var activity: MenuActivity

    @Before
    fun setup() {
        // Get context and resources before creating activity
        val context = ApplicationProvider.getApplicationContext<Context>()
        val resources = context.resources
        val resourceId = 0x7f070077

        try {
            val resourceName = resources.getResourceName(resourceId)
            Log.i("TestResource", "Resource name for ID 0x7f070077: $resourceName")
        } catch (e: Resources.NotFoundException) {
            Log.e("TestResource", "Resource 0x7f070077 not found in test environment")
        }

        // Attempt to create activity
        try {
            activity = Robolectric.buildActivity(MenuActivity::class.java).create().get()
        } catch (e: Exception) {
            Log.e("TestSetup", "Failed to create activity: ${e.message}")
            throw e // Re-throw to fail the test and see the stack trace
        }
    }

    @Test
    fun testButtonExists() {
        val createLobbyBtn = activity.findViewById<Button>(R.id.createLobbyBtn)
        assertNotNull(createLobbyBtn)
    }
}