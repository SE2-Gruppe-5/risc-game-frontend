package com.se2gruppe5.risikofrontend.game.cards

import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import kotlin.random.Random

class CardHandler :ICardHandler {

    /*
    Assign the correct amount of troops for the stars spent
    for lookup https://www.hasbro.com/common/documents/dad2886d1c4311ddbd0b0800200c9a66/655CBCEE50569047F53E6EB2C4990FB6.pdf
     */
    private fun getUnitsForStars(starCount: Int): Int {
        return when (starCount) {
            2 -> 2
            3 -> 4
            4 -> 7
            5 -> 10
            6 -> 13
            7 -> 17
            8 -> 21
            9 -> 25
            10 -> 30
            else -> -1
        }
    }

    override fun tradeCards(player: PlayerRecord, starCount: Int) {
        if(starCount <2 ||starCount >10){
            throw IllegalArgumentException("Wrong starcount for tradein specified")
        }
        player.freeTroops += getUnitsForStars(starCount);
    }

    override fun getCard(player: PlayerRecord) {
        //1 Star 60% of the time, else 2 Stars
        val starCount = if (Random.Default.nextFloat() < 0.6f) 1 else 2
        player.stars +=starCount
    }
}