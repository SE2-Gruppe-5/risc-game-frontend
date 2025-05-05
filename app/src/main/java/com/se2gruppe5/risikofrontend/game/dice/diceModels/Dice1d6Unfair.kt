package com.se2gruppe5.risikofrontend.game.dice.diceModels

import kotlin.random.Random

/**
 * A not-so-fair six-sided die
 * Returns a number 1-6, skewed towards higher rolls
 */
class Dice1d6Unfair : IDice {
    override fun roll(): Int {
        var rdmNumber = 0
        repeat(3) { //Best of 3
            val rdmNew = Random.Default.nextInt(1, 6 + 1)
            if (rdmNew > rdmNumber) {
                rdmNumber = rdmNew
            }
        }
        return rdmNumber
    }
}