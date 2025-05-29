package com.se2gruppe5.risikofrontend.game.dice.diceModels

import kotlin.random.Random

/**
 * A blatantly cheating six-sided die
 * Returns 6, always.
 */
class Dice1d6Cheating : IDice {

    constructor()

    //For compatibility.
    // This Dice does not require any RNG given the degree to which it is cheating.
    constructor(random: Random)

    override fun roll(): Int {
        return 6
    }
}
