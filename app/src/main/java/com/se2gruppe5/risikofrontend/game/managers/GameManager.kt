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
    var territoryVisualList : MutableList<Triple<TextView, ImageButton, View>> = mutableListOf()


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

        runBlocking {
            assignTerritories(GameManager.get().uuid) // Aufruf der Methode, um Territorien zuzuweisen
            distributeTroopsAutomatically() // Automatische Verteilung der Truppen
        }

        val viewManager = GameViewManager(activity)
        viewManager.setPlayerNames(players, turnIndicators)
    }
    suspend fun assignTerritories(gameId: UUID) {
        val client: INetworkClient = NetworkClient()  // Sicherstellen, dass die Client-Instanz verwendet wird
        client.assignTerritories(gameId)  // Aufruf der Methode mit dem gameId
    }
    fun distributeTroopsAutomatically() {
        // Beispiel für eine automatische Verteilung: Jeder Spieler erhält 5 Truppen
        val defaultTroops = 5
        for (player in players?.values ?: emptyList()) {
            runBlocking {
                distributeTroops(GameManager.get().uuid, defaultTroops)  // Verteile Truppen
            }
        }
    }

    suspend fun distributeTroops(gameId: UUID, troops: Int) {
        val client: INetworkClient = NetworkClient()  // Verwenden der NetworkClient-Instanz
        client.distributeTroops(gameId, troops)  // Aufruf der Methode mit dem gameId und den Truppen
    }



    /**
     * Initializes all territory txt,btn and outline and puts them into a List
     */
    private fun initTerritoryViews(activity: Activity) {
        territoryVisualList.add(Triple(activity.findViewById<TextView>(R.id.territoryAtext),activity.findViewById<ImageButton>(R.id.territoryAbtn), activity.findViewById<View>(R.id.territoryAoutline)))
        territoryVisualList.add(Triple(activity.findViewById<TextView>(R.id.territoryBtext),activity.findViewById<ImageButton>(R.id.territoryBbtn), activity.findViewById<View>(R.id.territoryBoutline)))

    }


    fun updateTerritories(t: List<TerritoryRecord>){
        for(i in t.indices){
            val t1 = t.get(i)
            val views = territoryVisualList.get(i)
            val t1_vis : ITerritoryVisual= TerritoryVisualAndroid(t1,views.first,views.first,views.second,views.third)
            t1.owner = players!!.get(uuidList!!.get(0))
            TerritoryManager.get().updateTerritory(t1_vis)
        }
    }
    val client : INetworkClient = NetworkClient()

    private fun updatePlayerRequest(pUUID: UUID, name: String, color: Int){
        runBlocking {
            client.updatePlayer(uuid,pUUID,name,color)
        }
    }







}