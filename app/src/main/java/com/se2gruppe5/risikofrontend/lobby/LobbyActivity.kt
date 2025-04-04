package com.se2gruppe5.risikofrontend.lobby

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity

class LobbyActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.lobby)


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

        val backBtn = this.findViewById<ImageButton>(R.id.backBtn)
        val startGameBtn = this.findViewById<Button>(R.id.startGameBtn)

        backBtn.setOnClickListener({
            Log.i("WEBCHAT", "Sending message: create")
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        })


    }

}