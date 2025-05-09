package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.graphics.Color
import com.se2gruppe5.risikofrontend.game.GameActivity
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
        fun getPlayers(): List<PlayerRecord>{
            val p1 = PlayerRecord(1, "Markus", Color.rgb(255, 100, 0))
            val p2 = PlayerRecord(2, "Leo", Color.rgb(0, 100, 255))
            return listOf(p1, p2)
        }
    }
    private var phase = Phases.Reinforce
    private var currentPlayerIndex = 0;
    private var currentPlayer = players[currentPlayerIndex];

    /**
     * Signals backend to swap to the next Player
     * Hands out a card if the Player captured a territory
     */
    private fun nextPlayer(): Pair<Phases, Int>{
        if(currentPlayer.capturedTerritory){
            CardHandler.getCard(currentPlayer);
            currentPlayer.capturedTerritory = false
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

    fun initializeGame(){


    }

    fun initializeBoard(activity: Activity){


    }







}