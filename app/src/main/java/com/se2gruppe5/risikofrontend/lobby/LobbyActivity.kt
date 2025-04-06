package com.se2gruppe5.risikofrontend.lobby

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.GameActivity

class LobbyActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.lobby)

        val playerName = intent.getStringExtra("PLAYER_NAME") ?: "Player"
        val ownNameTxt = findViewById<TextView>(R.id.ownNameTxt)
        ownNameTxt.text = playerName
        val namePlayer1 = findViewById<TextView>(R.id.namePlayer1)
        namePlayer1.text = playerName

        val joinCode = generateJoinCode()
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

        findViewById<ImageButton>(R.id.player2Btn).visibility = View.GONE
        findViewById<ImageButton>(R.id.player3Btn).visibility = View.GONE
        findViewById<ImageButton>(R.id.player4Btn).visibility = View.GONE
        findViewById<ImageButton>(R.id.player5Btn).visibility = View.GONE
        findViewById<ImageButton>(R.id.player6Btn).visibility = View.GONE

        findViewById<TextView>(R.id.namePlayer2).visibility = View.GONE
        findViewById<TextView>(R.id.namePlayer3).visibility = View.GONE
        findViewById<TextView>(R.id.namePlayer4).visibility = View.GONE
        findViewById<TextView>(R.id.namePlayer5).visibility = View.GONE
        findViewById<TextView>(R.id.namePlayer6).visibility = View.GONE

        val backBtn = this.findViewById<ImageButton>(R.id.backBtn)
        val startGameBtn = this.findViewById<Button>(R.id.startGameBtn)

        backBtn.setOnClickListener({
            Log.i("NAVIGATION", "Sending message: Quit lobby")
            val intent = Intent(this, CreateLobbyActivity::class.java)
            startActivity(intent)
        })
        startGameBtn.setOnClickListener({
            Log.i("NAVIGATION", "Sending message: Starting Game")
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        })


    }

}

fun generateJoinCode(length: Int = 4): String {
    val chars = ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}