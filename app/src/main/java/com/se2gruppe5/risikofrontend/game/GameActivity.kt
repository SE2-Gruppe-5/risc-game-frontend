package com.se2gruppe5.risikofrontend.game

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dice.DiceVisualAndroid
import com.se2gruppe5.risikofrontend.game.dice.dies.Dice1d6Generic
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.game)


        //Placeholder
        val diceBtn = this.findViewById<ImageButton>(R.id.diceButton)
        val diceTxt = this.findViewById<TextView>(R.id.diceText)
        val diceVisualAndroid = DiceVisualAndroid(Dice1d6Generic(), diceBtn, diceTxt)
        diceVisualAndroid.clickSubscription { it.roll() }

        val p1 = PlayerRecord(1, "Markus", Color.rgb(255, 100, 0))
        val p2 = PlayerRecord(2, "Leo", Color.rgb(0, 100, 255))

        //todo This is not pretty and hardcoded. It shouldn't be. It should be done by the GameManager
        val t1 = TerritoryRecord(1,10)
        val t1_txt = this.findViewById<TextView>(R.id.territoryAtext)
        val t1_btn = this.findViewById<ImageButton>(R.id.territoryAbtn)
        val t1_outline = this.findViewById<View>(R.id.territoryAoutline)
        val t1_vis: ITerritoryVisual =
            TerritoryVisualAndroid(t1, t1_txt, t1_txt, t1_btn, t1_outline)

        val t2 = TerritoryRecord(2,5)
        val t2_txt = this.findViewById<TextView>(R.id.territoryBtext)
        val t2_btn = this.findViewById<ImageButton>(R.id.territoryBbtn)
        val t2_outline = this.findViewById<View>(R.id.territoryBoutline)
        val t2_vis: ITerritoryVisual =
            TerritoryVisualAndroid(t2, t2_txt, t2_txt, t2_btn, t2_outline)

        val pointingArrow = PointingArrowAndroid(this, "#FF0000".toColorInt(), 15f)
        pointingArrow.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        findViewById<ViewGroup>(R.id.main).addView(pointingArrow)

        TerritoryManager.init(p1, pointingArrow)
        val territoryManager = TerritoryManager.get()
        territoryManager.addTerritory(t1_vis)
        territoryManager.addTerritory(t2_vis)

        territoryManager.assignOwner(t1_vis, p1)
        territoryManager.assignOwner(t2_vis, p2)


        this.findViewById<ImageButton>(R.id.goattack).setOnClickListener({
            territoryManager.enterSelectMode()
            territoryManager.enterAttackMode()
            this.findViewById<ImageButton>(R.id.goattack).backgroundTintList =
                ColorStateList.valueOf(Color.RED)
        })

        val nextPhaseBtn = this.findViewById<Button>(R.id.phaseBtn)
        val reinforceIndicator = this.findViewById<TextView>(R.id.reinforceIndicator)
        val attackIndicator = this.findViewById<TextView>(R.id.attackIndicator)
        val tradeIndicator = this.findViewById<TextView>(R.id.tradeIndicator)
        val phaseTxt = this.findViewById<TextView>(R.id.currentPhaseTxt)

        GameManager.init(listOf(p1,p2), p1)
        val gameManager = GameManager.get()
        val notYourTurnPopUp = buildNotYourTurnDialog();
        nextPhaseBtn.setOnClickListener {
            var phase = gameManager.nextPhase()
            when(phase){
                Phases.Reinforce -> {changeViewColors(reinforceIndicator,attackIndicator,tradeIndicator)
                    phaseTxt.text = "Reinforce"
                }
                Phases.Attack -> {changeViewColors(attackIndicator,reinforceIndicator,tradeIndicator)
                    phaseTxt.text = "Attack"
                    nextPhaseBtn.text = "End Turn"
                }
                Phases.Trade -> {changeViewColors(tradeIndicator,attackIndicator,reinforceIndicator)
                    phaseTxt.text = "Trade"
                    nextPhaseBtn.text = "Next Phase"
                }

                Phases.OtherPlayer -> {notYourTurnPopUp.show()}
            }


        }

    }
    val highlightedColor = Color.BLACK
    val notHighlightedColor = Color.GRAY

    private fun changeViewColors(highlighted: View, not1: View, not2: View){
        highlighted.setBackgroundColor(highlightedColor)
        not1.setBackgroundColor(notHighlightedColor)
        not2.setBackgroundColor(notHighlightedColor)
    }
    private fun buildNotYourTurnDialog(): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle("Wait for your turn")
            .setPositiveButton("ok") {dialog, which ->{}}
        return builder.create()

    }

}


