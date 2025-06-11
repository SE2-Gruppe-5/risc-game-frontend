package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType

object CardHandler :ICardHandler {



    override fun tradeCards(cards: List<CardRecord>): Int {
        if(cards.size ==3) {
            var infantry = 0
            var cavalry = 0
            var artillery = 0
            for(card in cards){
                    if (card.type == CardType.Infantry) infantry++
                    if (card.type == CardType.Cavalry) cavalry++
                    if (card.type == CardType.Artillery) artillery++
                }

            if (infantry == 3) {
                return 4
            } else if (cavalry == 3) {
                return 6
            } else if (artillery == 3) {
                return 8
            } else if (artillery == 1 && cavalry == 1 && infantry == 1) {
                return 10
            }
        }
        return -1
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
