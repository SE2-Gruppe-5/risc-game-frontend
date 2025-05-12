package com.se2gruppe5.risikofrontend.game.territory

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
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
}
