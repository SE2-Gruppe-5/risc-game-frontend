package com.se2gruppe5.risikofrontend.game.territory

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord

class GameViewManager(private val activity: Activity) {
    /**
     * Set the correct amount of Playertext
     * Change them to the correspending playernames
     */
    fun setPlayerNames(players: List<PlayerRecord>?, textViews: List<TextView>) {
        textViews.forEachIndexed { index, textView ->
            if (index < players!!.size) {
                textView.text = players[index].name
            } else {
                textView.visibility = View.GONE
            }
        }
    }
}