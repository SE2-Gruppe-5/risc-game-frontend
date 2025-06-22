package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.game.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.game.PlayerRecord

interface ICardHandler {

    /**
     * Trade a list of cards for troops
     * if the traded cards don't match the schema return -1
     * Else return the number of troops that the player gets from the trade
     */
    fun tradeCards(cards: List<CardRecord>): Int


    /**
     * Give player a card if they captured a Territory
     * Cards will be abstracted into only giving them 1 or 2 stars
     */
    fun getCard(player: PlayerRecord?)
}
