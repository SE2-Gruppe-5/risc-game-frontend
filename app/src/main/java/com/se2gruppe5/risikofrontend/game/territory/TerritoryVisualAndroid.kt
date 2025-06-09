package com.se2gruppe5.risikofrontend.game.territory

import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.util.Point2D
import java.util.UUID

/**
 * Concrete Implementation of Territory Visualization for Android
 * [!] For method comments please refer to Interface
 */
class TerritoryVisualAndroid(
    override val territoryRecord: TerritoryRecord,
    val bgColorRibbon: TextView,
    val textContent: TextView,
    val imgBTN: ImageButton,
    val bg: View,
    val outline: View,
) : ITerritoryVisual {

    init {
        setHighlightSelected(false)
        changeBgColor(territoryRecord.continent.color.toColorInt())
    }
    val backgroundHighlightColor: Int = Color.argb(255, 255, 255, 0)
    val backgroundNoHighlightColor: Int = Color.argb(0, 255, 255, 0)

    /**
     * Toggles visual selection-highlighting of Territory
     * @param highlighted Enable/Disable the Highlighting
     */
    override fun setHighlightSelected(highlighted: Boolean) {
        if (highlighted) {
            outline.setBackgroundColor(backgroundHighlightColor)
        } else {
            outline.setBackgroundColor(backgroundNoHighlightColor)
        }
    }

    override fun changeRibbonColor(color: Int) {
        bgColorRibbon.setBackgroundColor(color)
    }

    override fun changeBgColor(color: Int) {
        imgBTN.setBackgroundColor(color)
        bg.setBackgroundColor(color)
    }

    override fun changeStat(stat: Int) {
        require(!(stat <= 0 || stat > 99)) { "Invalid territory stat: $stat" }

        territoryRecord.stat = stat
        textContent.text = stat.toString()
    }

    override fun changeOwner(newOwner: UUID?) {
        territoryRecord.owner = newOwner
    }

    override fun clickSubscription(lambda: (ITerritoryVisual) -> Unit) {
        imgBTN.setOnClickListener {
            lambda(this)
        }
    }

    override fun getCoordinates(center: Boolean): Point2D {
        val location = IntArray(2)
        imgBTN.getLocationInWindow(location)
        var x = location[0].toFloat()
        var y = location[1].toFloat()
        if (center) {
            x += imgBTN.width / 2
            y += imgBTN.height / 2
        }
        return Point2D(x,y)
    }

    override fun getTerritoryId(): Int{
        return territoryRecord.id
    }

}
