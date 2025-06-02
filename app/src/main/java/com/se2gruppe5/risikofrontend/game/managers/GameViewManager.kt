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
        for(i in 0..4) {
            cardDisplays.get(i).visibility = View.INVISIBLE
        }

        for(i in 0..cards.size){
            cardDisplays.get(i).visibility = View.VISIBLE
            var cardtype = 0
            when(cards.get(i).type){
                Infantry -> cardtype = 1
                Cavalry -> cardtype = 2
                Artillery -> cardtype = 3
            }
            cardDisplays.get(i).text = cardtype.toString()
        }




    }

}
