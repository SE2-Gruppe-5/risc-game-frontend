package com.se2gruppe5.risikofrontend.game.dice.dies

/**
 * A blatantly cheating six-sided die
 * Returns 6, always.
 */
class Dice1d6Cheating : IDice {
    override fun roll(): Int {
        return 6
    }
}