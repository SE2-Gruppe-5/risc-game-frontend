package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.board.BoardVisualGeneratorAndroid
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.territory.IPointingArrowUI
import com.se2gruppe5.risikofrontend.game.territory.PointingArrowAndroid
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
        BoardVisualGeneratorAndroid.initTerritoryViews(activity)
        initArrow()
        setPlayerNames(gameManager.getPlayers(), turnIndicators)
    }
}
