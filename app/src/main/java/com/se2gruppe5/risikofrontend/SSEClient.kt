package com.se2gruppe5.risikofrontend

import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.EventSource
import java.net.URI

class SSEClient {
    private var eventHandler: EventHandler? = null
    private var eventSource: EventSource? = null;

    fun init(eventHandler: EventHandler) {
        this.eventHandler = eventHandler

        eventSource = EventSource.Builder(eventHandler, URI.create(Constants.HOST + Constants.SSE_URL))
            .build()
        eventSource!!.start()
    }
}