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
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dice.Dice1d6Generic
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

        tutorialBtn.setOnClickListener({
            Log.i("NAVIGATION", "Entering Tutorial")
        })

        //Placeholder
        val diceBtn = this.findViewById<ImageButton>(R.id.diceButton)
        val diceTxt = this.findViewById<TextView>(R.id.diceText)
        val diceVisualAndroid = DiceVisualAndroid(Dice1d6Generic(),diceBtn,diceTxt)
        diceVisualAndroid.clickSubscription {it.roll()}

        val p1 = PlayerRecord(1,"Markus", Color.rgb(255,100,0))
        val p2 = PlayerRecord(2, "Leo", Color.rgb(0,100,255))

        val t1 = TerritoryRecord(1)
        val t1_txt = this.findViewById<TextView>(R.id.territoryAtext)
        val t1_btn = this.findViewById<ImageButton>(R.id.territoryAbtn)
        val t1_outline = this.findViewById<View>(R.id.territoryAoutline)
        val t1_vis: ITerritoryVisual = TerritoryVisualAndroid(t1, t1_txt,t1_txt,t1_btn,t1_outline)

        val t2 = TerritoryRecord(2)
        val t2_txt = this.findViewById<TextView>(R.id.territoryBtext)
        val t2_btn = this.findViewById<ImageButton>(R.id.territoryBbtn)
        val t2_outline = this.findViewById<View>(R.id.territoryBoutline)
        val t2_vis: ITerritoryVisual = TerritoryVisualAndroid(t2,t2_txt,t2_txt,t2_btn,t2_outline)

        val pointingArrow = PointingArrowAndroid(this,"#FF0000".toColorInt(),15f)
        pointingArrow.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        findViewById<ViewGroup>(R.id.main).addView(pointingArrow)

        pointingArrow.setCoordinates(Pair(0f,0f),Pair(250f,250f))

        TerritoryManager.init(p1,pointingArrow)
        val territoryManager = TerritoryManager.get()
        territoryManager.addTerritory(t1_vis)
        territoryManager.addTerritory(t2_vis)

        territoryManager.assignOwner(t1_vis,p1)
        territoryManager.assignOwner(t2_vis,p2)


        this.findViewById<ImageButton>(R.id.goattack).setOnClickListener({
            territoryManager.enterSelectMode()
            territoryManager.enterAttackMode()
            this.findViewById<ImageButton>(R.id.goattack).backgroundTintList = ColorStateList.valueOf(Color.RED)
        })



        //-------------
    }


}
