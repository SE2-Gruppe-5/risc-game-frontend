package com.se2gruppe5.risikofrontend.players

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.SSEClient

class ActivePlayersActivity : AppCompatActivity(), PlayerUpdateListener {

    val _players = MutableLiveData<List<String>>()
    private lateinit var adapter: ArrayAdapter<String>

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

        // Initialisiere den SSE-Client mit dem PlayerEventHandler
        SSEClient().init(PlayerEventHandler(this))
    }

    // Implementierung von PlayerUpdateListener
    override fun onPlayersUpdated(players: List<String>) {
        // Aktualisiere die LiveData mit den neuen Spielern
        _players.value = players
    }
}
