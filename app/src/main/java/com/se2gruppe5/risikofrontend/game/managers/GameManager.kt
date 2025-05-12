package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.board.BoardLoaderAndroid
import com.se2gruppe5.risikofrontend.game.cards.CardHandler
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.GameViewManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.LineAndroid
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid
import com.se2gruppe5.risikofrontend.network.INetworkClient
import com.se2gruppe5.risikofrontend.network.NetworkClient
import java.util.UUID
import kotlinx.coroutines.runBlocking


class GameManager  private constructor(val me : PlayerRecord, val uuid : UUID){
    companion object {

        //Intentionally not using non-nullable lateInit var for unit test reset functionality
        private var singleton: GameManager? = null
        private var currentPlayer: PlayerRecord? = null
        private var currentPlayerUuid: UUID? = null
        private var currentPlayerIndex: Int? = 0
        private var phase = Phases.Reinforce
        private var players: HashMap<UUID, PlayerRecord>? = null
        private var uuidList : MutableList<UUID>? = mutableListOf()

        fun init(me : PlayerRecord, uuid: UUID, playerMap: HashMap<UUID, PlayerRecord>) {
            if (singleton==null) {
                singleton = GameManager(me, uuid)
                players = playerMap
                var uuidSet = players?.keys
                for(uuid in uuidSet!!){
                    uuidList!!.add(uuid)
                }


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
        fun getCurrentPlayerIndex(): Int? {
            return currentPlayerIndex
        }
        fun getPhase(): Phases{
            return phase
        }
        fun getCurrentPlayerUuidList(): MutableList<UUID>? {
            return uuidList
        }


        /**
         * Only for testing
         */
        fun setPhase(new: Phases){
            phase = new
        }

        fun updateCurrentPlayer(){
            if (players != null) {
                currentPlayerIndex = 0
                for( i in uuidList!!.indices){
                    currentPlayerIndex = currentPlayerIndex!! + 1
                    if (players!!.get(uuidList!!.get(i))?.isCurrentTurn == true){
                        currentPlayer = players!!.get(uuidList!!.get(i))
                        currentPlayerUuid = uuidList!!.get(i)
                        break
                    }
                }
            }
        }
    }
    val MAX_PLAYERS: Int = 6
    var territoryVisualMap : HashMap<Int, ITerritoryVisual> = HashMap()


    fun updatePlayers(playerMap: HashMap<UUID, PlayerRecord>): Int? {
        players = playerMap
        updateCurrentPlayer()
        return currentPlayerIndex
    }
    /**
     * Signals backend to swap to the next Player
     * Hands out a card if the Player captured a territory
     */
    fun nextPlayer(): Pair<Phases, Int?> {
        if(currentPlayer?.capturedTerritory == true){
            CardHandler.getCard(currentPlayer)
            currentPlayer!!.capturedTerritory = false
        }
        updatePlayerRequest(currentPlayer!!.id,currentPlayer!!.name,currentPlayer!!.color)
       updateCurrentPlayer()


        return Pair(Phases.Reinforce,currentPlayerIndex)
    }

    /**
     * Swaps Phase to the next one
     */
    fun nextPhase(toPhase: Phases): Pair<Phases, Int?> {
        if(currentPlayer == me) {
            when (toPhase) {
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
        initTerritoryViews(activity)
        val pointingArrow = PointingArrowAndroid(activity, "#FF0000".toColorInt(), 15f)
        pointingArrow.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        activity.findViewById<ViewGroup>(R.id.main).addView(pointingArrow)
        TerritoryManager.init(me, pointingArrow, activity)
        val viewManager = GameViewManager(activity)
        viewManager.setPlayerNames(players, turnIndicators)
    }

    /**
     * Initializes all territory txt,btn and outline and puts them into a List
     */
    private fun initTerritoryViews(activity: Activity) {
        val board = activity.findViewById<FrameLayout>(R.id.gameBoard)
        val territories: List<TerritoryRecord> = BoardLoaderAndroid(activity).getTerritories()

        val territoryManager = TerritoryManager.get()

        for(territory in territories) {
            val territoryLayout =
                LayoutInflater.from(activity).inflate(R.layout.territory_template, board, false)
            territoryLayout.id = View.generateViewId()

            val params = FrameLayout.LayoutParams(territory.size.first, territory.size.second)
            params.leftMargin = territory.position.first
            params.topMargin = territory.position.second
            territoryLayout.layoutParams = params

            val text = territoryLayout.findViewById<TextView>(R.id.territoryText)
            val button = territoryLayout.findViewById<ImageButton>(R.id.territoryBtn)
            val outline = territoryLayout.findViewById<View>(R.id.territoryOutline)
            board.addView(territoryLayout)

            val visual: ITerritoryVisual =
                TerritoryVisualAndroid(territory, text, text, button, outline)
            territoryManager.addTerritory(visual)
            territoryVisualMap[territory.id] = visual

            for(connected in territory.connections) {
                val line = LineAndroid(activity, "#000000".toColorInt(), 5f)
                val startCoordinates = intPairToFloatPair(territory.position)
                val endCoordinates = intPairToFloatPair(connected.position)
                line.z = 0f
                line.setCoordinates(startCoordinates, endCoordinates)
                line.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                activity.findViewById<ViewGroup>(R.id.gameBoard).addView(line)
            }
        }
    }


    fun updateTerritories(t: List<TerritoryRecord>){
        for(territory in t){
            val targetTerritory = territoryVisualMap[territory.id]!!
            targetTerritory.territoryRecord.owner = territory.owner
            targetTerritory.territoryRecord.stat = territory.stat
            targetTerritory.territoryRecord.owner = players!!.get(uuidList!!.get(0))
            TerritoryManager.get().updateTerritory(targetTerritory)
        }
    }
    val client : INetworkClient = NetworkClient()

    private fun updatePlayerRequest(pUUID: UUID, name: String, color: Int){
        runBlocking {
            client.updatePlayer(uuid,pUUID,name,color)
        }
    }

    private fun intPairToFloatPair(pair: Pair<Int, Int>): Pair<Float, Float> {
        return Pair(pair.first.toFloat(), pair.second.toFloat())
    }







}