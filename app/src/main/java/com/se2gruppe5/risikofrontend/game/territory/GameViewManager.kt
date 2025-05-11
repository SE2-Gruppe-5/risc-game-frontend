package com.se2gruppe5.risikofrontend.game.territory

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import java.util.UUID

class GameViewManager(private val activity: Activity) {
    /**
     * Set the correct amount of Playertext
     * Change them to the correspending playernames
     */
    fun setPlayerNames(players: HashMap<UUID, PlayerRecord>?, textViews: List<TextView>) {
        var uuidList = GameManager.getCurrentPlayerUuidList()
        textViews.forEachIndexed { index, textView ->
            if (index < players!!.size) {
                textView.text = players.get(uuidList!!.get(index))?.name
            } else {
                textView.visibility = View.GONE
            }
        }
    }
}