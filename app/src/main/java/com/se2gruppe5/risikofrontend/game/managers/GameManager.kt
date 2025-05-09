package com.se2gruppe5.risikofrontend.game.managers

import com.se2gruppe5.risikofrontend.game.cards.CardHandler
import com.se2gruppe5.risikofrontend.game.cards.ICardHandler
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.Phases
import java.lang.IllegalStateException


class GameManager  private constructor(val players: List<PlayerRecord>, val me : PlayerRecord){
    companion object {

        //Intentionally not using non-nullable lateInit var for unit test reset funcitonality
        private var singleton: GameManager? = null

        fun init(players:  List<PlayerRecord>, me : PlayerRecord) {
            if (singleton==null) {
                singleton = GameManager(players, me)
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
    }
    private var phase = Phases.Reinforce
    private var currentPlayerIndex = 0;
    private var currentPlayer = players[currentPlayerIndex];

    /**
     * Signals backend to swap to the next Player
     * Hands out a card if the Player captured a territory
     */
    private fun nextPlayer(): Phases{
        if(currentPlayer.capturedTerritory){
            CardHandler.getCard(currentPlayer);
        }
       if(currentPlayerIndex+1 == players.size){
           currentPlayerIndex = 0
       }else currentPlayerIndex++
        currentPlayer = players[currentPlayerIndex]


        return Phases.Reinforce
    }

    /**
     * Swaps Phase to the next one
     */
    fun nextPhase(): Phases {
        if(currentPlayer == me) {
            when (phase) {
                Phases.Reinforce -> phase = Phases.Attack
                Phases.Attack -> phase = Phases.Trade
                Phases.Trade -> phase = nextPlayer()
                else -> {}
            }
            return phase;
        }
        return Phases.OtherPlayer
    }








}