package com.se2gruppe5.risikofrontend.lobby

import android.content.Intent
import android.content.pm.ActivityInfo
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
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.GameActivity
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.network.NetworkClient
import com.se2gruppe5.risikofrontend.network.sse.MessageType
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import com.se2gruppe5.risikofrontend.network.sse.constructServiceConnection
import com.se2gruppe5.risikofrontend.network.sse.messages.GameStartMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.JoinLobbyMessage
import kotlinx.coroutines.runBlocking
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
                joinLobby(joinCode, playerName)
        }
    }
    var joinedPlayers: Int = 1
    var players: HashMap<UUID, PlayerRecord> = HashMap()
    var playerBtn: MutableList<ImageButton>? = mutableListOf()
    var playerTxt: MutableList<TextView>? = mutableListOf()
    var joinCode: String = ""
    var playerName: String = ""
    var me : PlayerRecord? = null
    var amILobbyOwner: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.lobby)

        playerName = intent.getStringExtra("PLAYER_NAME") ?: "Player"
        val ownNameTxt = findViewById<TextView>(R.id.ownNameTxt)
        ownNameTxt.text = playerName

        joinCode = intent.getStringExtra("LOBBY_CODE").toString()
        val lobbyCodeTxt = findViewById<TextView>(R.id.lobbyCodeTxt)
        lobbyCodeTxt.text = joinCode

        amILobbyOwner = intent.getBooleanExtra("AM_I_LOBBY_OWNER", false)


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

            playerBtn!![i].setOnLongClickListener {
                kickPlayer(playerTxt!![i].text.toString())
                true
            }
            playerTxt!![i].setOnLongClickListener {
                kickPlayer(playerTxt!![i].text.toString())
                true
            }
        }


        val backBtn = this.findViewById<ImageButton>(R.id.backBtn)
        val startGameBtn = this.findViewById<Button>(R.id.startGameBtn)

        backBtn.setOnClickListener {
            Log.i("NAVIGATION", "Quit lobby")
            finish()
        }
        startGameBtn.setOnClickListener {
            Log.i("NAVIGATION", "Starting Game")
            startGame(joinCode)
        }

    }

    private fun joinLobby(code: String, name: String) {
        runBlocking {
            client.joinLobby(code, name)
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

    private fun kickPlayer(name: String) {
        if (amILobbyOwner) {
            var player: PlayerRecord? = null
            for (p in players.values) {
                if (p.name == name) {
                    player = p
                    break
                }
            }

            if (player != null) {
                runBlocking {
                    client.leaveLobby(joinCode, player.id, "Kicked by owner")
                }
            }
        }
    }

    private fun setupHandlers(service: SseClientService) {
        sseService?.handler(MessageType.JOIN_LOBBY) {
            it as JoinLobbyMessage
            runOnUiThread {
                Log.i("lobby", "Hello from lobbyhandler")
                Log.i("lobby", "$it")
                val uuid: UUID = it.uuid
                val name: String = it.playerName
                playerTxt?.get(joinedPlayers - 1)?.visibility = View.VISIBLE
                playerTxt?.get(joinedPlayers - 1)?.text = name
                playerBtn?.get(joinedPlayers - 1)?.visibility = View.VISIBLE
                joinedPlayers++
                val player = PlayerRecord(
                    uuid,
                    name,
                    Color.rgb((0..255).random(), (0..255).random(), (0..255).random())
                )
                if (me == null && SseClientService.uuid == uuid) {
                    me = player
                }
                players[player.id] = player
                Log.i("LobbyJoin", player.toString())
            }
        }
        sseService?.handler(MessageType.START_GAME) {
            it as GameStartMessage
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("GAME_DATA", it)
                intent.putExtra("LOCAL_PLAYER", me)
                startActivity(intent)
        }
    }

    private fun startGame(code: String){
        runBlocking {
            client.startGame(code)
        }
    }
}





