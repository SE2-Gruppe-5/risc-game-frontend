package com.se2gruppe5.risikofrontend.game.dice.diceModels

import kotlin.random.Random

/**
 * A not-so-fair six-sided die
 * Returns a number 1-6, skewed towards higher rolls
 */
class Dice1d6Unfair : IDice {
    private val rng: Random

    constructor() {
        this.rng = Random.Default
    }

    constructor(random: Random) {
        this.rng = random
    }

    override fun roll(): Int {
        var rdmNumber = 0
        repeat(3) { //Best of 3
            val rdmNew = rng.nextInt(1, 6 + 1)
            if (rdmNew > rdmNumber) {
                rdmNumber = rdmNew
            }
        }
        return rdmNumber
    }
}
