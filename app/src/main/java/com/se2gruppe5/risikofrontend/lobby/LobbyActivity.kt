package com.se2gruppe5.risikofrontend.lobby

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.se2gruppe5.risikofrontend.network.sse.messages.ChatMessage
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import kotlinx.coroutines.runBlocking
import java.util.UUID

class LobbyActivity :AppCompatActivity() {

    var joinedPlayers : Int = 1
    var MAX_PLAYER : Int = 6
    var players : MutableList<PlayerRecord> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.lobby)

        val playerName = intent.getStringExtra("PLAYER_NAME") ?: "Player"
        val ownNameTxt = findViewById<TextView>(R.id.ownNameTxt)
        ownNameTxt.text = playerName

        val joinCode : String = intent.getStringExtra("LOBBY_CODE").toString()
        val lobbyCodeTxt = findViewById<TextView>(R.id.lobbyCodeTxt)
        lobbyCodeTxt.text = joinCode


        val player1Btn = this.findViewById<ImageButton>(R.id.player1Btn)
        val player2Btn = this.findViewById<ImageButton>(R.id.player2Btn)
        val player3Btn = this.findViewById<ImageButton>(R.id.player3Btn)
        val player4Btn = this.findViewById<ImageButton>(R.id.player4Btn)
        val player5Btn = this.findViewById<ImageButton>(R.id.player5Btn)
        val player6Btn = this.findViewById<ImageButton>(R.id.player6Btn)
        val player1Txt = this.findViewById<TextView>(R.id.namePlayer1)
        val player2Txt = this.findViewById<TextView>(R.id.namePlayer2)
        val player3Txt = this.findViewById<TextView>(R.id.namePlayer3)
        val player4Txt = this.findViewById<TextView>(R.id.namePlayer4)
        val player5Txt = this.findViewById<TextView>(R.id.namePlayer5)
        val player6Txt = this.findViewById<TextView>(R.id.namePlayer6)

        player1Txt.visibility = View.GONE
        player2Btn.visibility = View.GONE
        player3Btn.visibility = View.GONE
        player4Btn.visibility = View.GONE
        player5Btn.visibility = View.GONE
        player6Btn.visibility = View.GONE

        player1Txt.visibility = View.GONE
        player2Txt.visibility = View.GONE
        player3Txt.visibility = View.GONE
        player4Txt.visibility = View.GONE
        player5Txt.visibility = View.GONE
        player6Txt.visibility = View.GONE
        Log.i("lobby", "Hello from lobbyactivity ${Constants.SSE_SERVICE}")


        if(joinedPlayers == 1 ){
            joinLobby(joinCode,playerName)
        }
        Constants.SSE_SERVICE?.handler(MessageType.JOIN_LOBBY) { it as ChatMessage
            runOnUiThread {
                Log.i("lobby", "Hello from lobbyhandler")
                Log.i("lobby", "$it")
                var uuid : UUID = UUID.randomUUID()
                var name : String = "asaba"
                when(joinedPlayers){
                     1 -> {
                        player1Txt.visibility = View.VISIBLE
                         player1Btn.visibility = View.VISIBLE
                         player1Txt.text = it.toString()
                }
                    2 -> {
                        player2Txt.visibility = View.VISIBLE
                        player2Btn.visibility = View.VISIBLE
                    }
                    3 -> {
                        player3Txt.visibility = View.VISIBLE
                        player3Btn.visibility = View.VISIBLE
                    }
                    4 -> {
                        player4Txt.visibility = View.VISIBLE
                        player4Btn.visibility = View.VISIBLE
                    }
                    5 -> {
                        player5Txt.visibility = View.VISIBLE
                        player5Btn.visibility = View.VISIBLE
                    }
                    6 -> {
                        player6Txt.visibility = View.VISIBLE
                        player6Btn.visibility = View.VISIBLE
                    }
                }

                Log.i("Lobby", "$it")
                players.add(PlayerRecord(uuid,name, Color.rgb((0..255).random(),(0..255).random(),(0..255).random())))
            }
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
    private fun joinLobby(code: String, name: String){
        val networkClient : INetworkClient = NetworkClient()
        runBlocking {
            networkClient.joinLobby(code, name)
        }
    }

}



