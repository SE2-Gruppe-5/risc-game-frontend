package com.se2gruppe5.risikofrontend.game.managers


import com.se2gruppe5.risikofrontend.game.cards.CardHandler
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.network.INetworkClient
import kotlinx.coroutines.runBlocking
import java.util.UUID


class GameManager private constructor(
    private val me: PlayerRecord,
    private val gameManagerUUID: UUID,
    private val territoryManager: TerritoryManager,
    private val networkClient: INetworkClient,
    private var players: HashMap<UUID, PlayerRecord>,
    private var currentPlayerUUID: UUID,
    private var currentPhase: Phases,
    private var amICurrentlyCheating: Boolean = false,
    private var haveAlreadyBeenPunished: Boolean = false
) {
    companion object {

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

        fun reset() {
            singleton = null
        }
    }

    fun getPlayer(id: UUID): PlayerRecord? {
        return this.players[id]
    }

    fun getPlayers(): HashMap<UUID, PlayerRecord> {
        return this.players
    }

    fun getCurrentPlayer(): PlayerRecord {
        return this.players[this.currentPlayerUUID]
            ?: throw IllegalArgumentException("Current Player not found in player list")
    }

    fun setCurrentlyCheating(amICheating: Boolean) {
        amICurrentlyCheating = amICheating
        if (amICheating == false) {
            haveAlreadyBeenPunished = false
        }
    }

    fun getPhase(): Phases {
        return this.currentPhase
    }

    /**
     * Unit Test only, do not call
     */
    fun setPhase(p: Phases) {
        this.currentPhase = p
    }

    //todo sprint 3 refactoring possibility: PlayerManager
    fun playerUUIDSanityCheck(players: HashMap<UUID, PlayerRecord>) {
        var c = 0
        for (player in players) {
            check(player.value.id == player.key) { "UUID mismatch in Player Hashmap (this is very very bad)" }

            if (player.value.isCurrentTurn) {
                c++
            }
        }
        check(c > 0) { "Currently it's nobody's turn (this shouldn't be the case)" }
        check(c <= 1) { "It cannot be more than one player's turn at any given moment" }

    }

    /**
     * Get from server
     */
    fun receivePlayerListUpdate(playersUpdated: HashMap<UUID, PlayerRecord>) {
        playerUUIDSanityCheck(playersUpdated)
        this.players = playersUpdated
        for (player in this.players) {
            if (player.value.isCurrentTurn) {
                this.currentPlayerUUID = player.key
                if (isMyTurn()) {
                    me.freeTroops = me.freeTroops + territoryManager.calculateNewTroops(me)
                } else {
                    setCurrentlyCheating(false)
                }

                return
            }
        }
    }

    /**
     * Get from Server
     */
    fun receiveTerritoryListUpdate(territories: List<TerritoryRecord>) {
        this.territoryManager.updateTerritories(territories)
    }

    /**
     * Get form Server
     */
    fun receivePhaseUpdate(newPhase: Phases) {
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
            if (me.capturedTerritory) {
                CardHandler.getCard(me)
                me.capturedTerritory = false
            }
            return true
        }
        return false
    }

    //this single method right here prevents all hell from breaking lose
    fun isMyTurn(): Boolean {
        val currentPlayer = players[currentPlayerUUID]
        return currentPlayer?.equals(me) == true
    }

    fun getCurrentPhase(): Phases {
        return currentPhase
    }

    fun getUUID(): UUID {
        return gameManagerUUID
    }

    fun getTerritoryManager(): TerritoryManager {
        return territoryManager
    }

    fun checkIHaveBeenAccusedCheating(accusedPlayerUUID: UUID) {
        if (accusedPlayerUUID == me.id && amICurrentlyCheating && currentPlayerUUID == me.id) {
            if (!haveAlreadyBeenPunished) {
                punishMyselfForCheating();
            }
            haveAlreadyBeenPunished = true
        }
    }

    fun whoAmI(): PlayerRecord {
        return me
    }

    //omitRandom is only for tests!
    fun penalizeForClicking(omitRandom: Boolean = false) {
        //penalize by removing one unit from a random territory >1 units
        for (t: ITerritoryVisual in territoryManager.getTerritoryList().shuffled()) {
            if (t.territoryRecord.owner == me.id) {
                if (t.territoryRecord.stat > 1) {
                    val newT = TerritoryRecord(
                        t.territoryRecord.id,
                        t.territoryRecord.stat - 1,
                        t.territoryRecord.continent,
                        t.territoryRecord.transform
                    )
                    newT.owner = me.id
                    runBlocking {
                        networkClient.changeTerritory(gameManagerUUID, newT)
                    }
                    break
                }
            }
        }
    }

    //public for unit tests
    //omitRandom is only for tests!
    fun punishMyselfForCheating(omitRandom: Boolean = false) {
        //Set Troops of some (about 1/3 of all) territories to 1
        for (t: ITerritoryVisual in territoryManager.getTerritoryList()) {
            if (t.territoryRecord.owner == me.id && (((0..2).random() == 0) || omitRandom)) {

                val newT = TerritoryRecord(
                    t.territoryRecord.id,
                    1,
                    t.territoryRecord.continent,
                    t.territoryRecord.transform
                )
                newT.owner = me.id
                territoryManager.updateTerritory(newT)
                runBlocking {
                    networkClient.changeTerritory(gameManagerUUID, newT)
                }

            }
        }
    }

}
