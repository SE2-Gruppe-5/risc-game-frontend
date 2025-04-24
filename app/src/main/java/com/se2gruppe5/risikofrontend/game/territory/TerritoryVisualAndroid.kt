package com.se2gruppe5.risikofrontend.game.territory

import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord

/**
 * Concrete Implementation of Territory Visualization for Android
 * [!] For method comments please refer to Interface
 */
class TerritoryVisualAndroid(
    override val territoryRecord: TerritoryRecord,
    val bgColorRibbon: TextView,
    val textContent: TextView,
    val imgBTN: ImageButton,
    val outline: View
) : ITerritoryVisual {

    init {
        setHighlightSelected(false)
    }

    override fun setHighlightSelected(b: Boolean) {
        if (b) {
            outline.setBackgroundColor(Color.argb(0, 255, 255, 0))
        } else {
            outline.setBackgroundColor(Color.argb(0, 255, 255, 0))
        }
    }

    override fun changeColor(color: Int) {
        try {
            bgColorRibbon.setBackgroundColor(color)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to apply color: $color")
        }
    }

    override fun changeStat(stat: Int) {
        if (stat <= 0 || stat > 99) {
            throw IllegalArgumentException("Invalid territory stat: $stat")
        }
        territoryRecord.stat = stat
        textContent.text = stat.toString()
    }

    override fun clickSubscription(lambda: (ITerritoryVisual) -> Unit) {
        imgBTN.setOnClickListener {
            lambda(this)
        }
    }

    override fun getCoordinatesAsFloat(center: Boolean): Pair<Float, Float> {
        val location = IntArray(2)
        imgBTN.getLocationInWindow(location)
        var x = location[0].toFloat()
        var y = location[1].toFloat()
        if (center) {
            x += imgBTN.width / 2
            y += imgBTN.height / 2
        }
        return Pair(x, y)
    }

}