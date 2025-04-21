package com.se2gruppe5.risikofrontend.startmenu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.interactables.DiceButton
import com.se2gruppe5.risikofrontend.lobby.CreateLobbyActivity
import com.se2gruppe5.risikofrontend.lobby.JoinLobbyActivity


class MenuActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_menu)

        val createLobbyBtn = this.findViewById<Button>(R.id.createLobbyBtn)
        val joinLobbyBtn = this.findViewById<Button>(R.id.joinLobbyBtn)
        val tutorialBtn = this.findViewById<Button>(R.id.tutorialBtn)
        createLobbyBtn.setOnClickListener({
            Log.i("NAVIGATION", "Creating Lobby")
            val intent = Intent(this, CreateLobbyActivity::class.java)
            startActivity(intent)
        })

        joinLobbyBtn.setOnClickListener({
            Log.i("NAVIGATION", "Joining Lobby")
            val intent = Intent(this, JoinLobbyActivity::class.java)
            startActivity(intent)
        })

        tutorialBtn.setOnClickListener({
            Log.i("NAVIGATION", "Entering Tutorial")
        })
        //Placeholder
        val diceBtn = this.findViewById<ImageButton>(R.id.diceButton)
        val diceTxt = this.findViewById<TextView>(R.id.diceText)
        DiceButton(diceBtn,diceTxt,1,6)
        //-------------
    }


}
