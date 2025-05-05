package com.se2gruppe5.risikofrontend.game.dice

import com.se2gruppe5.risikofrontend.game.dice.diceModels.IDice

interface IDiceVisual {
    /**
     * Wrapped Dice-Model (e.g. "Fair", "Unfair" or "Cheating")
     * //todo: could possibly be needed mutable with getter and setter in future
     */
    val dice: IDice

    /**
     * Roll the dice!
     */
    fun roll()

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
     * **Default implementation registers roll()**
     */
    fun clickSubscription(lambda: (IDiceVisual) -> Unit)
}