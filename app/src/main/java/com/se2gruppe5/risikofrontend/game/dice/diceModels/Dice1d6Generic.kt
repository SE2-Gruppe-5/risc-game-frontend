package com.se2gruppe5.risikofrontend.game.dice.diceModels

import kotlin.random.Random

/**
 * A fair six-sided die
 * Returns a number 1-6
 */
class Dice1d6Generic : IDice {
    private val rng: Random

    constructor() {
        this.rng = Random.Default
    }

    constructor(random: Random) {
        this.rng = random
    }

    override fun roll(): Int {
        return rng.nextInt(1, 6 + 1)
    }
}
