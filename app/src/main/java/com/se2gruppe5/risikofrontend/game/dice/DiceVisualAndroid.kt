package com.se2gruppe5.risikofrontend.game.dice

import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dice.diceHardware.IDiceHardware
import com.se2gruppe5.risikofrontend.game.dice.diceModels.Dice1d6Generic
import com.se2gruppe5.risikofrontend.game.dice.diceModels.IDice
import com.se2gruppe5.risikofrontend.game.dice.popups.ShakePhoneAlert

/**
 *  @param dice *OPTIONAL* setDice() via constructor param
 *  @param imgBTN Reference to the Image Button which rolls the dice!
 *  @param txt *OPTIONAL* Reference to the TextView(/extends) for displaying dice result.
 */
class DiceVisualAndroid(
    dice: IDice? = null,
    val imgBTN: ImageButton,
    val txt: TextView? = null,
    val diceHardware: IDiceHardware? = null,
    val diceShakePopup: ShakePhoneAlert? = null
) : IDiceVisual {

    //To facilitate backwards compatibility (i.e. setting the die by constructor param)
    //If omitted, default to standard dice-model.
    private var diceModel: IDice = dice ?: Dice1d6Generic()

    override fun setDice(dice: IDice) {
        diceModel = dice
    }

    override fun getDice(): IDice {
        return diceModel
    }

    /**
     * Subscribe to this with clickSubscription for simply rolling the dice by button press
     */
    override fun roll() {
        val result = diceModel.roll()
        if (txt != null) {
            txt.text = result.toString()
        }
        //Hide popup if dice roll occurred
        diceShakePopup?.dismissShakePromptDialog()
    }

    /**
     * Subscribe to this with clickSubscription for initiating Hardware-Roll-Interaction
     * (HW omitted if not set, calls roll() instead)
     */
    override fun hwInteraction(){
        //If there is no hardware... do not use it.
        if(diceHardware==null){
            roll()
        }
        diceHardware?.setInteractionLambdaSubscription { this.roll() }
        diceShakePopup?.showShakePromptDialog()
        diceHardware?.sensorRegisterListener()
    }

    override fun clickSubscription(lambda: (IDiceVisual) -> Unit) {
        imgBTN.setOnClickListener {
            lambda(this)
        }
    }


}
