package com.se2gruppe5.risikofrontend.game.interactables

import android.widget.ImageButton
import android.widget.TextView
import kotlin.random.Random

/**
 * Business Logic Object for Dice
 * (Initializes Dice and setOnClickListener)
 * @param imgBTN Reference to the Image Button which rolls the dice!
 * @param txt *OPTIONAL* Reference to the TextView(/extends) for displaying dice result.
 * @param fromRange Lowest Possible Number (inclusive)
 * @param toRange Highest Possible Number (inclusive)
 */
class DiceButton(imgBTN: ImageButton, txt: TextView? = null, fromRange: Int = 1, toRange: Int = 6) {
    init {
        //Button is already a non-null type by Android; null check omitted
        imgBTN.setOnClickListener {
            //Generate Random Number between fromRange and toRange
            var rdmNumber = Random.Default.nextInt(fromRange, toRange + 1)
            //Permit txt to be null explicitly as it is optional
            if (txt != null) {
                txt.text = rdmNumber.toString()
            }
        }
    }
}