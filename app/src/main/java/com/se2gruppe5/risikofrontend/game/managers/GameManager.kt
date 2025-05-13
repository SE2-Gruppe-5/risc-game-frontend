package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid
import com.se2gruppe5.risikofrontend.network.INetworkClient
import java.util.UUID


class GameManager private constructor(
    private val me: PlayerRecord,
    private val gameManagerUUID: UUID,
    private val territoryManager: TerritoryManager,
    private val networkClient: INetworkClient,
    private var players: HashMap<UUID, PlayerRecord>,
    private var currentPlayerUUID: UUID,
    private var currentPhase: Phases
) {
    companion object {

        //Intentionally not using non-nullable lateInit var for unit test reset functionality
        private var singleton: GameManager? = null

        fun init(
            me: PlayerRecord,
            gameManagerUUID: UUID,
            territoryManager: TerritoryManager,
            networkClient: INetworkClient,
            players: HashMap<UUID, PlayerRecord>
        ) {
            if (this.singleton == null) {
                this.singleton = GameManager(
                    me,
                    gameManagerUUID,
                    territoryManager,
                    networkClient,
                    players,
                    me.id,
                    Phases.Reinforce
                )
            }
        }

        //Throws when null [i.e. .get() before .init()]
        fun get(): GameManager {
            return checkNotNull(singleton) { "GameManager must be .init() first!" }
        }

        /**
         * Do not call this. It is for unit tests only.
         */
        fun unitTestReset() {
            singleton = null
        }
    }


    fun getPlayers(): HashMap<UUID, PlayerRecord>? {
        return this.players
    }

    fun getCurrentPlayer(): PlayerRecord {
        return this.players[this.currentPlayerUUID]
            ?: throw IllegalArgumentException("Current Player not found in player list")
    }

    fun getPhase(): Phases {
        return this.currentPhase
    }

    var territoryVisualList: MutableList<Triple<TextView, ImageButton, View>> = mutableListOf()

    //todo sprint 3 refactoring possibility: PlayerManager
    private fun playerUUIDSanityCheck(players: HashMap<UUID, PlayerRecord>) {
        var c: Int = 0
        for (player in this.players) {
            if (player.value.id != player.key) {
                throw IllegalStateException("UUID mismatch in Player Hashmap (this is very very bad)")
            }

            if (player.value.isCurrentTurn == true) {
                c++
            }
        }
        if (c <= 0) {
            throw IllegalStateException("Currently it's nobody's turn (this shouldn't be the case)")
        } else if (c > 1) {
            throw IllegalStateException("It cannot be more than one player's turn at any given moment")
        }

    }

    /**
     * Get from server
     */
    fun receivePlayerListUpdate(playersUpdated: HashMap<UUID, PlayerRecord>) {
        playerUUIDSanityCheck(playersUpdated)
        this.players = playersUpdated
        for (player in this.players) {
            if (player.value.isCurrentTurn == true) {
                this.currentPlayerUUID = player.key
                return
            }
        }
    }

    /**
     * Get from Server
     */
    fun receiveTerritoryListUpdate(territories: List<TerritoryRecord>){
        this.territoryManager.updateTerritories(territories)
    }

    /**
     * Get form Server
     */
    fun receivePhaseUpdate(newPhase: Phases){
        this.currentPhase = newPhase
    }

    // NOTE: THE SERVER TAKES CARE OF SETTING THE NEXT PLAYER'S TURN
    // (hence no function for that here)
    // THIS IS TO IMPOSE AN ORDER

    /**
     * Swaps Phase to the next one
     */
    suspend fun nextPhase(): Boolean {
        if (isMyTurn()) {
            networkClient.changePhase(gameManagerUUID)
            return true
        }
        return false
    }

    //this single method right here prevents all hell from breaking lose
    fun isMyTurn(): Boolean {
        val currentPlayer = players[currentPlayerUUID]
        return currentPlayer?.equals(me) == true
    }


    /**
     * Function to initialize the Gameboard
     */
    fun initializeGame(activity: Activity, turnIndicators: List<TextView>) {
        val viewManager = GameViewManager(activity)
        territoryVisualList = viewManager.initTerritoryViews()
        TerritoryManager.init(me, viewManager.initArrow(), activity)
        territoryVisualList.forEachIndexed { index, tri ->
            val territory = TerritoryVisualAndroid(
                TerritoryRecord(index, 5),
                tri.first,
                tri.first,
                tri.second,
                tri.third
            )
            TerritoryManager.get().addTerritory(territory)
        }
        viewManager.setPlayerNames(players, turnIndicators)
    }



    fun getCurrentPhase(): Phases{
        return currentPhase
    }

    fun getUUID(): UUID{
        return gameManagerUUID
    }

    fun getTerritoryManager(): TerritoryManager{
        return territoryManager
    }

    fun whoAmI(): PlayerRecord{
        return me
    }


}