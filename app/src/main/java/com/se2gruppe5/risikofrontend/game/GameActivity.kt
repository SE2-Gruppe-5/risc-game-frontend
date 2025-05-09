package com.se2gruppe5.risikofrontend.game

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.board.BoardLoaderAndroid
import com.se2gruppe5.risikofrontend.game.board.Continent
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dice.DiceVisualAndroid
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Generic
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.game)

//-------------------------------------- <Placeholder>
        val diceBtn = this.findViewById<ImageButton>(R.id.diceButton)
        val diceTxt = this.findViewById<TextView>(R.id.diceText)
        val diceVisualAndroid = DiceVisualAndroid(Dice1d6Generic(), diceBtn, diceTxt)
        diceVisualAndroid.clickSubscription { it.roll() }

        val p1 = PlayerRecord(1, "Markus", Color.rgb(255, 100, 0))
        val p2 = PlayerRecord(2, "Leo", Color.rgb(0, 100, 255))

        val board = findViewById<FrameLayout>(R.id.gameBoard)
        val territories: List<TerritoryRecord> = BoardLoaderAndroid(this).getTerritories()

        val pointingArrow = PointingArrowAndroid(this, "#FF0000".toColorInt(), 15f)
        pointingArrow.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        findViewById<ViewGroup>(R.id.main).addView(pointingArrow)

        TerritoryManager.init(p1, pointingArrow)
        val territoryManager = TerritoryManager.get()

        for(territory in territories) {
            val territoryLayout = LayoutInflater.from(this).inflate(R.layout.territory_template, board, false)
            territoryLayout.id = View.generateViewId()

            val params = FrameLayout.LayoutParams(territory.size.first, territory.size.second)
            params.leftMargin = territory.position.first
            params.topMargin = territory.position.second
            territoryLayout.layoutParams = params

            val text = territoryLayout.findViewById<TextView>(R.id.territoryText)
            val button = territoryLayout.findViewById<ImageButton>(R.id.territoryBtn)
            val outline = territoryLayout.findViewById<View>(R.id.territoryOutline)
            board.addView(territoryLayout)

            val visual: ITerritoryVisual = TerritoryVisualAndroid(territory, text, text, button, outline)
            territoryManager.addTerritory(visual)

            // Only for testing purposes, should be replaced
            if(territory.id == 1) {
                territoryManager.assignOwner(visual, p1)
            }
            if(territory.id == 2) {
                territoryManager.assignOwner(visual, p2)
            }
        }

        this.findViewById<ImageButton>(R.id.goattack).setOnClickListener({
            territoryManager.enterSelectMode()
            territoryManager.enterAttackMode()
            this.findViewById<ImageButton>(R.id.goattack).backgroundTintList =
                ColorStateList.valueOf(Color.RED)
        })

//-------------------------------------- </Placeholder>
    }


}