package com.se2gruppe5.risikofrontend.startmenu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.ReinforcementActivity
import com.se2gruppe5.risikofrontend.lobby.CreateLobbyActivity
import com.se2gruppe5.risikofrontend.lobby.JoinLobbyActivity
import com.se2gruppe5.risikofrontend.network.NetworkClient
import com.se2gruppe5.risikofrontend.network.sse.MessageType
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import com.se2gruppe5.risikofrontend.network.sse.constructServiceConnection
import com.se2gruppe5.risikofrontend.network.sse.messages.ChatMessage


class MenuActivity : AppCompatActivity() {

    val client = NetworkClient()
    var sseService: SseClientService? = null
    val serviceConnection = constructServiceConnection { service ->
        // Allow network calls on main thread for testing purposes
        // GitHub Actions Android emulator action with stricter policy fails otherwise
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )
        sseService = service
    }
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        setContentView(R.layout.start_menu)

        val createLobbyBtn = this.findViewById<Button>(R.id.createLobbyBtn)
        val joinLobbyBtn = this.findViewById<Button>(R.id.joinLobbyBtn)
        val tutorialBtn = this.findViewById<Button>(R.id.tutorialBtn)
        createLobbyBtn.setOnClickListener {
            Log.i("NAVIGATION", "Creating Lobby")
            val intent = Intent(this, CreateLobbyActivity::class.java)
            startActivity(intent)
        }

        joinLobbyBtn.setOnClickListener {
            Log.i("NAVIGATION", "Joining Lobby")
            val intent = Intent(this, JoinLobbyActivity::class.java)
            startActivity(intent)
        }


        tutorialBtn.setOnClickListener {
            Log.i("NAVIGATION", "Entering Tutorial")
            val intent = Intent(this, ReinforcementActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onStart() {
        super.onStart()
        Intent(this, SseClientService::class.java).also {
            startService(it)
            bindService(it, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (sseService != null) {
            unbindService(serviceConnection)
        }
    }



}
