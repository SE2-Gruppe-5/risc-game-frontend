package com.se2gruppe5.risikofrontend.game.managers

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.transition.Visibility
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.board.BoardVisualGeneratorAndroid
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.enums.CardType.*
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

    /**
     * Displays the cards a Player has
     */
    fun updateCardDisplay(player: PlayerRecord) {
        val cardDisplays = mutableListOf<TextView>()
        cardDisplays.add(activity.findViewById<TextView>(R.id.card1))
        cardDisplays.add(activity.findViewById<TextView>(R.id.card2))
        cardDisplays.add(activity.findViewById<TextView>(R.id.card3))
        cardDisplays.add(activity.findViewById<TextView>(R.id.card4))
        cardDisplays.add(activity.findViewById<TextView>(R.id.card5))

       val  cards = player.cards
        for(display in cardDisplays) {
            display.visibility = View.INVISIBLE
        }
        if(cards.isNotEmpty()) {
            for (i in cards.indices) {
                cardDisplays[i].visibility = View.VISIBLE
                val cardtype = when (cards[i].type) {
                    Infantry -> 1
                    Cavalry -> 2
                    Artillery -> 3
                }
                cardDisplays[i].text = cardtype.toString()
            }
        }



    }

}
