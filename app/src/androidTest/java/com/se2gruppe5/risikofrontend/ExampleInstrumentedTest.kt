package com.se2gruppe5.risikofrontend

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

// Can be run using ``gradle connectedAndroidTest``. Requires a running (emulated) Android device.
// Not run as a GitHub action, and we doubt that it is necessary either way.

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.se2gruppe5.risikofrontend", appContext.packageName)
    }
}
