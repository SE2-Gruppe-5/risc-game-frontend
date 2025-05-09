package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.cards.CardHandler
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid


class GameManager  private constructor(val players: List<PlayerRecord>, val me : PlayerRecord){
    companion object {

        //Intentionally not using non-nullable lateInit var for unit test reset functionality
        private var singleton: GameManager? = null
        var currentPlayerIndex = 0
        var currentPlayer: PlayerRecord? = null
        var phase = Phases.Reinforce
        fun init(players:  List<PlayerRecord>, me : PlayerRecord) {
            if (singleton==null) {
                singleton = GameManager(players, me)
                currentPlayer = players[currentPlayerIndex]
            }
        }

        //Throws when null [i.e. .get() before .init()]
        fun get(): GameManager {
            return checkNotNull(singleton) { "GameManager must be .init() first!" }
        }

        /**
         * Do not call this. It is for unit tests only.
         */
        fun unitTestReset(){
            singleton=null
        }
        fun getPlayers(): List<PlayerRecord>{
            val p1 = PlayerRecord(1, "Markus", Color.rgb(255, 100, 0))
            val p2 = PlayerRecord(2, "Leo", Color.rgb(0, 100, 255))
            return listOf(p1, p2)
        }
    }
    val MAX_PLAYERS: Int = 6

    /**
     * Signals backend to swap to the next Player
     * Hands out a card if the Player captured a territory
     */
    private fun nextPlayer(): Pair<Phases, Int>{
        if(currentPlayer?.capturedTerritory == true){
            CardHandler.getCard(currentPlayer)
            currentPlayer!!.capturedTerritory = false
        }
       if(currentPlayerIndex + 1 == players.size){
           currentPlayerIndex = 0
       }else currentPlayerIndex++
        currentPlayer = players[currentPlayerIndex]


        return Pair(Phases.Reinforce,currentPlayerIndex)
    }

    /**
     * Swaps Phase to the next one
     */
    fun nextPhase(): Pair<Phases, Int> {
        if(currentPlayer == me) {
            when (phase) {
                Phases.Reinforce -> phase = Phases.Attack
                Phases.Attack -> phase = Phases.Trade
                Phases.Trade -> {
                    var temp = nextPlayer()
                    phase = temp.first
                    return temp
                }
                else -> {}
            }

            return Pair(phase, currentPlayerIndex);
        }
        return Pair(Phases.OtherPlayer,currentPlayerIndex)
    }



    /**
     * Function to initialize the Gameboard
     */
    fun initializeGame(activity: Activity, turnIndicators: List<TextView>){
        initializeBoard(activity)
        setPlayerNames(activity, turnIndicators)
    }

    /**
     * Set the correct amount of Playertext
     * Change them to the correspending playernames
     */
    private fun setPlayerNames(activity: Activity, playerNames: List<TextView>) {
        var i = 0
        while(i < players.size){
            playerNames[i].text = players[i].name
            i++
        }
        while (i < MAX_PLAYERS){
            playerNames[i].visibility = GONE
            i++
        }
    }

    /**
     * Create all Territories
     * Initialize TerritoryManager
     */
    private fun initializeBoard(activity: Activity){
        //TODO Territories, correct setup for TerritoryManager
        //still the code from markus just extracted
        val p1 = players[0]
        val p2 = players[1]
        val t1 = TerritoryRecord(1,10)
        val t1_txt = activity.findViewById<TextView>(R.id.territoryAtext)
        val t1_btn = activity.findViewById<ImageButton>(R.id.territoryAbtn)
        val t1_outline = activity.findViewById<View>(R.id.territoryAoutline)
        val t1_vis: ITerritoryVisual =
            TerritoryVisualAndroid(t1, t1_txt, t1_txt, t1_btn, t1_outline)

        val t2 = TerritoryRecord(2,5)
        val t2_txt = activity.findViewById<TextView>(R.id.territoryBtext)
        val t2_btn = activity.findViewById<ImageButton>(R.id.territoryBbtn)
        val t2_outline = activity.findViewById<View>(R.id.territoryBoutline)
        val t2_vis: ITerritoryVisual =
            TerritoryVisualAndroid(t2, t2_txt, t2_txt, t2_btn, t2_outline)

        val pointingArrow = PointingArrowAndroid(activity, "#FF0000".toColorInt(), 15f)
        pointingArrow.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        activity.findViewById<ViewGroup>(R.id.main).addView(pointingArrow)

        TerritoryManager.init(p1, pointingArrow, activity)
        val territoryManager = TerritoryManager.get()
        territoryManager.addTerritory(t1_vis)
        territoryManager.addTerritory(t2_vis)
        territoryManager.assignOwner(t1_vis, p1)
        territoryManager.assignOwner(t2_vis, p2)


    }







}