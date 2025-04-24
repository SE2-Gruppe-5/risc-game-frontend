package com.se2gruppe5.risikofrontend.game.territory

import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord

/**
 * Concrete Implementation of Territory Visualization for Android
 * [!] For method comments please refer to Interface
 */
class TerritoryVisualAndroid(override val territoryRecord: TerritoryRecord, val bgColorRibbon: TextView, val textContent: TextView, val imgBTN: ImageButton) : ITerritoryVisual{

    override fun setHighlightSelected(b: Boolean) {
        TODO("Not yet implemented")
    }

    override fun changeColor(color: Int) {
        bgColorRibbon.setBackgroundColor(color)
    }

    override fun changeStat() {
        TODO("Not yet implemented")
    }

    override fun clickSubscription(lambda: (ITerritoryVisual) -> Unit) {
        imgBTN.setOnClickListener {
            lambda(this)
        }
    }

    override fun getCoordinatesAsFloat(): Pair<Float, Float> {
        val location = IntArray(2)
        imgBTN.getLocationInWindow(location)
        return Pair(location[0].toFloat(),location[1].toFloat())
    }

}