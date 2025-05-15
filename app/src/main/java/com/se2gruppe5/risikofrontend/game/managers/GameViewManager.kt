package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.board.BoardLoaderAndroid
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.territory.IPointingArrowUI
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.game.territory.LineAndroid
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid
import java.util.UUID

class GameViewManager(private val activity: Activity) {

    private fun setPlayerNames(players: HashMap<UUID, PlayerRecord>?, textViews: List<TextView>) {
        if (players == null) {
            textViews.forEach { it.visibility = View.GONE }
            return
        }

        val playerList = players.values.toList()

        textViews.forEachIndexed { index, textView ->
            if (index < playerList.size) {
                textView.text = playerList[index].name
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
            }
        }
    }


    /**
     * Initializes all territory views based on the game board definition and displays them
     */
    private fun initTerritoryViews() {
        val board = activity.findViewById<FrameLayout>(R.id.gameBoard)
        val territories: List<TerritoryRecord> = BoardLoaderAndroid(activity).getTerritories()

        val territoryManager = TerritoryManager.get()

        for(territory in territories) {
            val territoryLayout =
                LayoutInflater.from(activity).inflate(R.layout.territory_template, board, false)
            territoryLayout.id = View.generateViewId()

            val params = FrameLayout.LayoutParams(territory.size.first, territory.size.second)
            params.leftMargin = territory.position.first
            params.topMargin = territory.position.second
            territoryLayout.layoutParams = params

            val text = territoryLayout.findViewById<TextView>(R.id.territoryText)
            val button = territoryLayout.findViewById<ImageButton>(R.id.territoryBtn)
            val outline = territoryLayout.findViewById<View>(R.id.territoryOutline)
            board.addView(territoryLayout)

            val visual: ITerritoryVisual =
                TerritoryVisualAndroid(territory, text, text, button, outline)
            territoryManager.addTerritory(visual)

            for(connected in territory.connections) {
                val line = LineAndroid(activity, "#000000".toColorInt(), 5f)
                val startCoordinates = intPairToFloatPair(territory.position)
                val endCoordinates = intPairToFloatPair(connected.position)
                line.z = 0f
                line.setCoordinates(startCoordinates, endCoordinates)
                line.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                activity.findViewById<ViewGroup>(R.id.gameBoard).addView(line)
            }
        }
    }

    private fun intPairToFloatPair(pair: Pair<Int, Int>): Pair<Float, Float> {
        return Pair(pair.first.toFloat(), pair.second.toFloat())
    }

    /**
     * initializes the Pointing arrow
     */
    private fun initArrow(): IPointingArrowUI {
        val pointingArrow = TerritoryManager.get().pointingArrow as PointingArrowAndroid
        pointingArrow.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        activity.findViewById<ViewGroup>(R.id.main).addView(pointingArrow)
        return pointingArrow
    }

    /**
     * Function to initialize the Gameboard
     */
    fun initializeGame(activity: Activity, turnIndicators: List<TextView>) {
        val gameManager = GameManager.get()
        initTerritoryViews()
        initArrow()
        setPlayerNames(gameManager.getPlayers(), turnIndicators)
    }
}