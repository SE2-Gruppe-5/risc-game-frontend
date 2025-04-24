package com.se2gruppe5.risikofrontend.startmenu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.GameActivity
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dice.dies.Dice1d6Generic
import com.se2gruppe5.risikofrontend.game.dice.DiceVisualAndroid
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
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

        val debugBTN = this.findViewById<Button>(R.id.debugme)
        debugBTN.setOnClickListener({
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        })

        tutorialBtn.setOnClickListener({
            Log.i("NAVIGATION", "Entering Tutorial")
        })

    }


}
