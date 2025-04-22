package com.se2gruppe5.risikofrontend.game.territoryIO

import android.widget.ImageButton
import android.widget.TextView

class TerritoryUIAndroid(val bgColorRibbon: TextView, val textContent: TextView, val imgBTN: ImageButton) : ITerritoryUIWrapper{


    override fun highlightSelected() {
        TODO("Not yet implemented")
    }

    override fun changeColor(color: Int) {
        bgColorRibbon.setBackgroundColor(color)
    }

    override fun changeStat() {
        TODO("Not yet implemented")
    }

    override fun subscribeLambda(lambda: () -> Unit) {
        imgBTN.setOnClickListener {
            lambda()
        }
    }

}