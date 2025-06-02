package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord

interface ICardHandler {

    /**
     * Trade a list of cards for troops
     */
    fun tradeCards(player: PlayerRecord, cards: List<CardRecord>, starCount: Int)


    /**
     * Give player a card if they captured a Territory
     * Cards will be abstracted into only giving them 1 or 2 stars
     */
    fun getCard(player: PlayerRecord?)


}
