package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType

object CardHandler :ICardHandler {



    override fun tradeCards(cards: List<CardRecord>): Int {
        if (cards.size != 3) return -1

        val counts = cards.groupingBy { it.type }.eachCount()
        val infantry = counts[CardType.Infantry] ?: 0
        val cavalry = counts[CardType.Cavalry] ?: 0
        val artillery = counts[CardType.Artillery] ?: 0

        return when {
            infantry == 3 -> 4
            cavalry == 3 -> 6
            artillery == 3 -> 8
            infantry == 1 && cavalry == 1 && artillery == 1 -> 10
            else -> -1
        }
    }




    override fun getCard(player: PlayerRecord?) {
        if(player != null) {
            val rand = (1..3).random()
            when (rand) {
                1 -> {
                    player.cards.add(CardRecord(CardType.Infantry))
                }
                2 -> {
                    player.cards.add(CardRecord(CardType.Cavalry))
                }
                3 -> {
                    player.cards.add(CardRecord(CardType.Artillery))
                }
            }
        }else throw IllegalArgumentException ("Call with valid Player")
    }
}
