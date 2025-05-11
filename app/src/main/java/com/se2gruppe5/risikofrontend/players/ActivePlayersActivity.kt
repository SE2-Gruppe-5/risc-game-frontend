package com.se2gruppe5.risikofrontend.players

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.network.sse.MessageType
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import com.se2gruppe5.risikofrontend.network.sse.constructServiceConnection

class ActivePlayersActivity : AppCompatActivity(), PlayerUpdateListener {

    val _players = MutableLiveData<List<String>>()
    private lateinit var adapter: ArrayAdapter<String>
    private var sseService: SseClientService? = null

    private val serviceConnection = constructServiceConnection { service ->
        sseService = service
        service?.handler(MessageType.ACTIVE_PLAYERS) { message ->
            val players = (message as ActivePlayersMessage).players
            runOnUiThread {
                _players.value = players
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_players)

        val listView = findViewById<ListView>(R.id.playerListView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        // Beobachte LiveData, um die Liste der Spieler zu aktualisieren
        _players.observe(this) { players ->
            adapter.clear()
            adapter.addAll(players)
        }
    }
    override fun onStart() {
        super.onStart()
        Intent(this, SseClientService::class.java).also {
            bindService(it, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

    // Implementierung von PlayerUpdateListener
    override fun onPlayersUpdated(players: List<String>) {
        // Aktualisiere die LiveData mit den neuen Spielern
        _players.value = players
    }
}
