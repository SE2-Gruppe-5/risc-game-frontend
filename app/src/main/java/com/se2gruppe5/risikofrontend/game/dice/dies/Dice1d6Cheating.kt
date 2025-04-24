package com.se2gruppe5.risikofrontend.game.dice.dies

import kotlin.random.Random

/**
 * A not-so-fair six-sided die
 * Returns a number 1-6, skewed towards higher rolls
 */
class Dice1d6Cheating: IDice{
    override fun roll(): Int {
        var rdmNumber = Random.Default.nextInt(1, 6 + 1)
        var rdmNew = 0
        repeat(2) { //Best of 3
            rdmNew = Random.Default.nextInt(1, 6 + 1)
            if(rdmNew>rdmNumber){
                rdmNumber = rdmNew
            }
        }
        return rdmNumber
    }
}