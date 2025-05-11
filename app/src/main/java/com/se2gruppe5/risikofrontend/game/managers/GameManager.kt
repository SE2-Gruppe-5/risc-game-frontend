package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.cards.CardHandler
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.GameViewManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid
import java.util.UUID


class GameManager  private constructor(val me : PlayerRecord, val uuid : UUID){
    companion object {

        //Intentionally not using non-nullable lateInit var for unit test reset functionality
        private var singleton: GameManager? = null
        private var currentPlayer: PlayerRecord? = null
        private var currentPlayerIndex: UUID? = null
        private var phase = Phases.Reinforce
        private var players: HashMap<UUID, PlayerRecord>? = null
        private var uuidSet : Set<UUID>? = mutableSetOf()

        fun init(me : PlayerRecord, uuid: UUID, playerMap: HashMap<UUID, PlayerRecord>) {
            if (singleton==null) {
                singleton = GameManager(me, uuid)
                players = playerMap
                uuidSet = players?.keys
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
        private fun getPlayers(): List<PlayerRecord>{
            return listOf()
        }
        fun getCurrentPlayer() : PlayerRecord? {
            return currentPlayer
        }
        fun getCurrentPlayerIndex(): UUID? {
            return currentPlayerIndex
        }
        fun getPhase(): Phases{
            return phase
        }

        /**
         * Only for testing
         */
        fun setPhase(new: Phases){
            phase = new
        }

        fun updateCurrentPlayer(){
            if (players != null) {
                for( i in uuidSet!!){
                    if (players!!.get(i)?.isCurrentTurn == true){
                        currentPlayer = players!!.get(i)
                        currentPlayerIndex = i
                        break
                    }
                }
            }
        }
    }
    val MAX_PLAYERS: Int = 6

    /**
     * Signals backend to swap to the next Player
     * Hands out a card if the Player captured a territory
     */
    fun nextPlayer(): Pair<Phases, UUID?> {
        if(currentPlayer?.capturedTerritory == true){
            CardHandler.getCard(currentPlayer)
            currentPlayer!!.capturedTerritory = false
        }
       updateCurrentPlayer()

        return Pair(Phases.Reinforce,currentPlayerIndex)
    }

    /**
     * Swaps Phase to the next one
     */
    fun nextPhase(): Pair<Phases, UUID?> {
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

            return Pair(phase, currentPlayerIndex)
        }
        return Pair(Phases.OtherPlayer,currentPlayerIndex)
    }



    /**
     * Function to initialize the Gameboard
     */
    fun initializeGame(activity: Activity, turnIndicators: List<TextView>){
        initializeBoard(activity)
        val viewManager = GameViewManager(activity)
        viewManager.setPlayerNames(players, turnIndicators)
    }


    /**
     * Create all Territories
     * Initialize TerritoryManager
     */
    private fun initializeBoard(activity: Activity){
        //TODO Territories, correct setup for TerritoryManager
        //still the code from markus just extracted
        var p1 : PlayerRecord? = null
        var p2 : PlayerRecord? = null
        for(i in uuidSet!!){
            if(p1 != null){
                p1 = players!!.get(i)
                continue
            }
            p2 = players!!.get(i)
        }


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