package com.se2gruppe5.risikofrontend.game.dice

import com.se2gruppe5.risikofrontend.game.dice.diceModels.IDice

interface IDiceVisual {
    /**
     * Wrapped Dice-Model (e.g. "Fair", "Unfair" or "Cheating")
     */
    fun setDice(dice: IDice)
    fun getDice(): IDice
    fun resetDice()

    /**
     * Roll the dice!
     */
    fun roll()


    /**
     * Return the dice value after a dice roll
     * As soon as it is read, further calls (until the next dice roll) return null
     */
    fun getValue(): Int?

    /**
     * Perform input interaction with device hardware
     */
    fun hwInteraction()

    /**
     * Observer-Pattern like system
     * Pass a functions that is to be executed upon a dice-roll being issued by button click (e.g.)
     * Multiple may be provided
     * (This is to abstract away the onClickListener implementation detail)
     * A reference to whom called the function is passed as a param
     *
     * For APKs with different event systems,
     * additional adapters to facilitate the lambda functions may be needed)
     *
     */
    fun clickSubscription(lambda: (IDiceVisual) -> Unit)
}
