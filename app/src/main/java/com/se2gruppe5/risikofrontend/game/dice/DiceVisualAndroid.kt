package com.se2gruppe5.risikofrontend.game.dice

import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dice.diceHardware.DiceHardwareAndroid
import com.se2gruppe5.risikofrontend.game.dice.diceHardware.IDiceHardware
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Generic
import com.se2gruppe5.risikofrontend.game.dice.diceModels.IDice

/**
 *  @param dice *OPTIONAL* setDice() via constructor param
 *  @param imgBTN Reference to the Image Button which rolls the dice!
 *  @param txt *OPTIONAL* Reference to the TextView(/extends) for displaying dice result.
 */
class DiceVisualAndroid(
    dice: IDice? = null,
    val imgBTN: ImageButton,
    val txt: TextView? = null,
    diceHardware: IDiceHardware? = null
) : IDiceVisual {

    //To facilitate backwards compatibility (i.e. setting the die by constructor param)
    //If omitted, default to standard dice-model.
    private var diceModel: IDice = dice ?: Dice1d6Generic()

    private val diceHW: IDiceHardware = diceHardware ?: DiceHardwareAndroid()

    override fun setDice(dice: IDice) {
        diceModel = dice
    }

    override fun getDice(): IDice {
        return diceModel
    }

    override fun roll() {
        val result = diceModel.roll()
        if (txt != null) {
            txt.text = result.toString()
        }
    }
    override fun hwInteraction(){
        diceHW
        roll()

    }

    override fun clickSubscription(lambda: (IDiceVisual) -> Unit) {
        imgBTN.setOnClickListener {
            lambda(this)
        }
    }

}
