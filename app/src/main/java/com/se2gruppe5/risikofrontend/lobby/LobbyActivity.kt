package com.se2gruppe5.risikofrontend.lobby

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.Constants
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.GameActivity
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.network.INetworkClient
import com.se2gruppe5.risikofrontend.network.NetworkClient
import com.se2gruppe5.risikofrontend.network.sse.MessageType
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import com.se2gruppe5.risikofrontend.network.sse.constructServiceConnection
import com.se2gruppe5.risikofrontend.network.sse.messages.ChatMessage
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text
import java.util.UUID

class LobbyActivity :AppCompatActivity() {
    val client = NetworkClient()
    var sseService: SseClientService? = null
    val serviceConnection = constructServiceConnection { service ->
        // Allow network calls on main thread for testing purposes
        // GitHub Actions Android emulator action with stricter policy fails otherwise
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )
        sseService = service
        if (service != null) {
            setupHandlers(service)
        }
    }
    var joinedPlayers: Int = 1
    var MAX_PLAYER: Int = 6
    var players: MutableList<PlayerRecord> = mutableListOf()
    var playerBtn: MutableList<ImageButton>? = mutableListOf()
    var playerTxt: MutableList<TextView>? = mutableListOf()
    var joinCode: String = ""
    var playerName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.lobby)

        playerName = intent.getStringExtra("PLAYER_NAME") ?: "Player"
        val ownNameTxt = findViewById<TextView>(R.id.ownNameTxt)
        ownNameTxt.text = playerName

        joinCode = intent.getStringExtra("LOBBY_CODE").toString()
        val lobbyCodeTxt = findViewById<TextView>(R.id.lobbyCodeTxt)
        lobbyCodeTxt.text = joinCode


        playerBtn?.add(this.findViewById<ImageButton>(R.id.player1Btn))
        playerBtn?.add(this.findViewById<ImageButton>(R.id.player2Btn))
        playerBtn?.add(this.findViewById<ImageButton>(R.id.player3Btn))
        playerBtn?.add(this.findViewById<ImageButton>(R.id.player4Btn))
        playerBtn?.add(this.findViewById<ImageButton>(R.id.player5Btn))
        playerBtn?.add(this.findViewById<ImageButton>(R.id.player6Btn))
        playerTxt?.add(this.findViewById<TextView>(R.id.namePlayer1))
        playerTxt?.add(this.findViewById<TextView>(R.id.namePlayer2))
        playerTxt?.add(this.findViewById<TextView>(R.id.namePlayer3))
        playerTxt?.add(this.findViewById<TextView>(R.id.namePlayer4))
        playerTxt?.add(this.findViewById<TextView>(R.id.namePlayer5))
        playerTxt?.add(this.findViewById<TextView>(R.id.namePlayer6))


        for (i in playerTxt?.indices!!) {
            playerBtn!![i].visibility = View.GONE
            playerTxt!![i].visibility = View.GONE
        }
        if (joinedPlayers == 1) {
            joinLobby(joinCode, playerName)
        }

        val backBtn = this.findViewById<ImageButton>(R.id.backBtn)
        val startGameBtn = this.findViewById<Button>(R.id.startGameBtn)

        backBtn.setOnClickListener({
            Log.i("NAVIGATION", "Quit lobby")
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        })
        startGameBtn.setOnClickListener({
            Log.i("NAVIGATION", "Starting Game")
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        })

    }

    private fun joinLobby(code: String, name: String) {
        val networkClient: INetworkClient = NetworkClient()
        runBlocking {
            networkClient.joinLobby(code, name)
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
        if (sseService != null) {
            unbindService(serviceConnection)
        }
    }

    private fun setupHandlers(service: SseClientService) {
        sseService?.handler(MessageType.JOIN_LOBBY) {
            it as ChatMessage
            runOnUiThread {
                Log.i("lobby", "Hello from lobbyhandler")
                Log.i("lobby", "$it")
                var uuid: UUID = UUID.randomUUID()
                var name: String = "asaba"
                playerTxt?.get(joinedPlayers-1)?.visibility = View.VISIBLE
                playerTxt?.get(joinedPlayers-1)?.text = name
                playerBtn?.get(joinedPlayers-1)?.visibility = View.VISIBLE

                joinedPlayers++
                players.add(
                    PlayerRecord(
                        uuid,
                        name,
                        Color.rgb((0..255).random(), (0..255).random(), (0..255).random())
                    )
                )
            }

        }
    }
}





