package com.se2gruppe5.risikofrontend.Players

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import androidx.lifecycle.MutableLiveData
import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import com.se2gruppe5.risikofrontend.SSEClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.os.Handler
import android.os.Looper

fun <T> setLiveDataValue(liveData: MutableLiveData<T>, value: T) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        liveData.value = value
    } else {
        Handler(Looper.getMainLooper()).post {
            liveData.value = value
        }
    }
}

class playerEventHandler(private val liveData: MutableLiveData<List<String>>) : EventHandler {

    override fun onOpen() {
        Log.i("SSE-LIFECYCLE", "onOpen") //NOSONAR - Logging only
    }

    override fun onClosed() {
        Log.i("SSE-LIFECYCLE", "onClosed") //NOSONAR - Logging only
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
        setLiveDataValue(liveData, playerList)
    }

    override fun onComment(comment: String?) {
        Log.i("SSE-LIFECYCLE", "onComment: $comment") // NOSONAR - Logging only
    }

    override fun onError(t: Throwable?) {
        Log.e("SSE-LIFECYCLE", "onError", t) // NOSONAR - Logging only
    }
}
// NOSONAR - UI-Komponente, nicht von Code Coverage erfasst
class ActivePlayersViewModel : AppCompatActivity() {
    val _players = MutableLiveData<List<String>>()
    private lateinit var adapter: ArrayAdapter<String>

    // NOSONAR - Android UI Setup, wird nicht unit-getestet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_players)

        val listView = findViewById<ListView>(R.id.playerListView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter


        _players.observe(this) { players ->
            adapter.clear()
            adapter.addAll(players)
        }

// NOSONAR - Netzwerkverhalten, wird nicht getestet
SSEClient().init(playerEventHandler(_players))

    }
}