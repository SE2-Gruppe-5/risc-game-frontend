package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType

object CardHandler :ICardHandler {


    //TODO
    override fun tradeCards(player: PlayerRecord, cards: List<CardRecord>, starCount: Int) {
//        var infantry = 0
//        var cavalry = 0
//        var artillery = 0
//        cards.forEach { card ->{
//            if (card.type == CardType.Infantry) infantry++
//            if(card.type == CardType.Cavalry) cavalry++
//            if(card.type == CardType.Artillery) artillery++
//        } }


    }



    override fun getCard(player: PlayerRecord?) {
        if(player != null) {
            val rand = (1..3).random()
            if (rand == 1) {
                player.cards.add(CardRecord(CardType.Infantry))
            } else if (rand == 2) {
                player.cards.add(CardRecord(CardType.Cavalry))
            } else if (rand == 3) {
                player.cards.add(CardRecord(CardType.Artillery))
            }
        }else throw IllegalArgumentException ("Call with valid Player")
    }
}