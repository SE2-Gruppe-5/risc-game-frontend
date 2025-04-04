package com.se2gruppe5.risikofrontend

import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.launchdarkly.eventsource.MessageEvent
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


/**
 *  !!! Beware: For running on local machine the Android Emulator must be turned on.
 */

@RunWith(AndroidJUnit4::class) //<-- Android VM Test
class AppendingHandlerInstrumentedTest {

    /**
     * Tests whether incoming messages are being displayed in the Textview.
     */
    @Test
    fun test_incomingMessageBeingDisplayed() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val textView = TextView(context)
        val handler = AppendingHandler(textView)
        //Message
        val messageData = "Hi, cool message :)"

        // Receive Message
        handler.onMessage("some-event", MessageEvent(messageData))

        // Assert messageData being displayed
        val result = textView.text.toString()
        assertTrue(result.contains(messageData))
    }

    /**
     * (For running Boilerplate at least once, not necessarily very good test)
     * Tests whether an incoming comment is also being displayed
     * (it can be but let's assume it shouldn't for the sake of the test)
     */
    @Test
    fun test_incomingCommentBeingIgnored() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val textView = TextView(context)
        val handler = AppendingHandler(textView)
        //Comment-Message
        handler.onComment("Don't you ever dare to display me!")
        // Assert comment NOT being displayed (remains empty)
        assertTrue(textView.text.isEmpty())
    }

    /**
     * (For running Boilerplate at least once, not necessarily very good test)
     * Check whether exceptions are handled "gracefully"...
     * so effectively meaning: the user at least not seeing it ;)
     */
    @Test
    fun test_incomingExceptionBeingHandled() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val textView = TextView(context)
        val handler = AppendingHandler(textView)
        //Error-Message
        val exception = RuntimeException("Very Horrible Error")
        handler.onError(exception)
        // Assert error NOT being displayed (remains empty)
        assertTrue(textView.text.isEmpty())
    }

    /**
     * (For running Boilerplate at least once, not necessarily very good test)
     * For now let's assume we just want nothing to be displayed when opening the handler...
     */
    @Test
    fun test_openHandler_boilerplate() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val textView = TextView(context)
        val handler = AppendingHandler(textView)

        // Open Handler
        handler.onOpen()
        // Nothing displayed, remains empty
        assertTrue(textView.text.isEmpty())
    }

    /**
     * (For running Boilerplate at least once, not necessarily very good test)
     * For now let's assume we just want nothing to be displayed when closing the handler...
     */
    @Test
    fun test_closeHandler_boilerplate() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val textView = TextView(context)
        val handler = AppendingHandler(textView)

        // Close Handler
        handler.onClosed()
        // Nothing displayed, remains empty
        assertTrue(textView.text.isEmpty())
    }
}
