package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord

interface ICardHandler {

    /**
     * Trade stars to get new troops
     */
    fun tradeCards(player: PlayerRecord, starCount: Int)


    /**
     * Give player a card if they captured a Territory
     * Cards will be abstracted into only giving them 1 or 2 stars
     */
    fun getCard(player: PlayerRecord)









}