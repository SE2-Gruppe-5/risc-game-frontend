package com.se2gruppe5.risikofrontend.game.dice

import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dice.diceModels.IDice

/**
 *  @param imgBTN Reference to the Image Button which rolls the dice!
 *  @param txt *OPTIONAL* Reference to the TextView(/extends) for displaying dice result.
 */
class DiceVisualAndroid(override val dice: IDice, val imgBTN: ImageButton, val txt: TextView? = null) :
    IDiceVisual {
    override fun roll() {
        val result = dice.roll()
        if (txt != null) {
            txt.text = result.toString()
        }
    }

    override fun clickSubscription(lambda: (IDiceVisual) -> Unit) {
        imgBTN.setOnClickListener {
            lambda(this)
        }
    }

}
