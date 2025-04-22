package com.se2gruppe5.risikofrontend.startmenu

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.territoryIO.ITerritoryUIWrapper
import com.se2gruppe5.risikofrontend.game.territoryIO.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territoryIO.TerritoryUIAndroid
import com.se2gruppe5.risikofrontend.game.dataclasses.Player
import com.se2gruppe5.risikofrontend.game.dataclasses.Territory
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

        //Playerholder

        val p1 = Player(1,"Markus", Color.rgb(255,100,0))
        val p2 = Player(2, "Leo", Color.rgb(0,100,255))

        val t1_txt = this.findViewById<TextView>(R.id.territoryAtext)
        val t1_btn = this.findViewById<ImageButton>(R.id.territoryAbtn)
        val t1_vis: ITerritoryUIWrapper = TerritoryUIAndroid(t1_txt,t1_txt,t1_btn)
        val t1 = Territory(1, t1_vis)

        val t2_txt = this.findViewById<TextView>(R.id.territoryBtext)
        val t2_btn = this.findViewById<ImageButton>(R.id.territoryBbtn)
        val t2_vis: ITerritoryUIWrapper = TerritoryUIAndroid(t2_txt,t2_txt,t2_btn)
        val t2 = Territory(2, t2_vis)

        val territoryManager: TerritoryManager = TerritoryManager()
        territoryManager.addTerritory(t1)
        territoryManager.addTerritory(t2)

        territoryManager.assignOwner(t1,p1)
        territoryManager.assignOwner(t2,p2)

        //-------------
    }


}
