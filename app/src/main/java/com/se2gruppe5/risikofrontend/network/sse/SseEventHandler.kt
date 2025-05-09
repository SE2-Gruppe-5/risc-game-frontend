package com.se2gruppe5.risikofrontend.network.sse

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import java.util.Base64

class SseEventHandler(val client: SseClientService) : EventHandler {
    companion object {
        private val gson = Gson()
    }

    override fun onOpen() {
    }

    override fun onClosed() {
    }

    override fun onMessage(event: String?, messageEvent: MessageEvent?) {
        if (event == null || messageEvent == null) {
            return
        }

        val type = getMessageType(event)
        if (type == null) {
            Log.w("SSE_EVENT", "Unknown event type: $event")
            return
        }

        val payload = Base64.getDecoder().decode(messageEvent.data).decodeToString()
        val data = gson.fromJson(payload, JsonObject::class.java)
        val message = type.deserialize(data)

        if (message == null) {
            Log.w("SSE_EVENT", "Couldn't deserialize $event: $payload")
            return
        }

        client.handleIncomingMessage(type, message)
    }

    override fun onComment(comment: String?) {
    }

    override fun onError(t: Throwable?) {
    }

}