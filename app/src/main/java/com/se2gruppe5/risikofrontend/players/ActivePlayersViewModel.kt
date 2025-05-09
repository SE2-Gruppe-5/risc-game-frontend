package com.se2gruppe5.risikofrontend.players

import android.util.Log
import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlayerEventHandler(private val listener: PlayerUpdateListener) : EventHandler {

    override fun onOpen() {
        Log.i("SSE-LIFECYCLE", "onOpen")
    }

    override fun onClosed() {
        Log.i("SSE-LIFECYCLE", "onClosed")
    }

    override fun onMessage(event: String?, messageEvent: MessageEvent?) {
        messageEvent?.let {
            try {
                handlePlayerMessage(it.data)
            } catch (e: Exception) {
                Log.e("SSE-LIFECYCLE", "Error parsing message: ${it.data}", e)
            }
        }
    }

    fun handlePlayerMessage(json: String) {
        val gson = Gson()
        val playerList: List<String> = gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
        listener.onPlayersUpdated(playerList)
    }

    override fun onComment(comment: String?) {
        Log.i("SSE-LIFECYCLE", "onComment: $comment")
    }

    override fun onError(t: Throwable?) {
        Log.e("SSE-LIFECYCLE", "onError", t)
    }
}