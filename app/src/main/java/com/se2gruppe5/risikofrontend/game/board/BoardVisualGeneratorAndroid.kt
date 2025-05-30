package com.se2gruppe5.risikofrontend.game.board

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.LineAndroid
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid

class BoardVisualGeneratorAndroid {
    companion object {

        /**
         * Initializes all territory views based on the game board definition and displays them
         */
        fun initTerritoryViews(activity: Activity, standalone: Boolean = false) {
            val board = activity.findViewById<FrameLayout>(R.id.gameBoard)
            val territories: List<TerritoryRecord> = BoardLoaderAndroid(activity).getTerritories()

            for(territory in territories) {
                val territoryLayout =
                    LayoutInflater.from(activity).inflate(R.layout.territory_template, board, false)
                territoryLayout.id = View.generateViewId()

                val params = FrameLayout.LayoutParams(territory.transform.size.x.toInt(), territory.transform.size.y.toInt())
                params.leftMargin = territory.transform.position.x.toInt()
                params.topMargin = territory.transform.position.y.toInt()
                territoryLayout.layoutParams = params

                val text = territoryLayout.findViewById<TextView>(R.id.territoryText)
                val button = territoryLayout.findViewById<ImageButton>(R.id.territoryBtn)
                val outline = territoryLayout.findViewById<View>(R.id.territoryOutline)
                val bg = territoryLayout.findViewById<View>(R.id.territoryBg)
                board.addView(territoryLayout)

                val visual: ITerritoryVisual =
                    TerritoryVisualAndroid(territory, text, text, button, bg, outline)

                if(!standalone) {
                    val territoryManager = TerritoryManager.get()
                    territoryManager.addTerritory(visual)
                }

                for(connected in territory.connections) {
                    val line = LineAndroid(activity, "#000000".toColorInt(), 5f)
                    val startCoordinates = territory.getCenter()
                    val endCoordinates = connected.getCenter()
                    line.z = -1f
                    line.setCoordinates(startCoordinates, endCoordinates)
                    line.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    activity.findViewById<ViewGroup>(R.id.gameBoard).addView(line)
                }
            }
        }
    }
}
