package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.game.territory.IPointingArrowUI
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
import com.se2gruppe5.risikofrontend.game.territory.TerritoryVisualAndroid
import java.util.UUID

class GameViewManager(private val activity: Activity) {

    fun setPlayerNames(players: HashMap<UUID, PlayerRecord>?, textViews: List<TextView>) {
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
     * Initializes all territory txt,btn and outline and puts them into a List
     */
    fun initTerritoryViews(): MutableList<Triple<TextView, ImageButton, View>> {
        var territoryVisualList: MutableList<Triple<TextView, ImageButton, View>> = mutableListOf()
        territoryVisualList.add(
            Triple(
                activity.findViewById<TextView>(R.id.territoryAtext),
                activity.findViewById<ImageButton>(R.id.territoryAbtn),
                activity.findViewById<View>(R.id.territoryAoutline)
            )
        )
        territoryVisualList.add(
            Triple(
                activity.findViewById<TextView>(R.id.territoryBtext),
                activity.findViewById<ImageButton>(R.id.territoryBbtn),
                activity.findViewById<View>(R.id.territoryBoutline)
            )
        )
        return territoryVisualList
    }

    /**
     * initializes the Pointing arrow
     */
    fun initArrow(): IPointingArrowUI {
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
        gameManager.territoryVisualList = initTerritoryViews()
        gameManager.territoryVisualList.forEachIndexed { index, tri ->
            val territory = TerritoryVisualAndroid(
                TerritoryRecord(index + 1, 5),
                tri.first,
                tri.first,
                tri.second,
                tri.third
            )
            TerritoryManager.get().addTerritory(territory)
        }

        initArrow()
        setPlayerNames(gameManager.getPlayers(), turnIndicators)
    }
}