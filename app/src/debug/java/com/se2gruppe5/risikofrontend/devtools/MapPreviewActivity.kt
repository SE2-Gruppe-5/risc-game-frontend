package com.se2gruppe5.risikofrontend.devtools

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.board.BoardVisualGeneratorAndroid
import com.se2gruppe5.risikofrontend.game.managers.GameManager
import com.se2gruppe5.risikofrontend.game.managers.TerritoryManager
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import com.se2gruppe5.risikofrontend.network.NetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapPreviewActivity: AppCompatActivity() {
    private var cheatUsedThisRound = false
    private var cheatModeEnabled = true
    private val networkClient = NetworkClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.devtools_map_preview)


        BoardVisualGeneratorAndroid.initTerritoryViews(this, cheatModeEnabled)

        setupCheatButton()
    }

    fun setupCheatButton() {
        val cheatButton = findViewById<Button>(R.id.cheatButton)
        cheatButton.visibility = if (cheatModeEnabled) View.VISIBLE else View.GONE

        cheatButton.setOnClickListener {
            val gameId = GameManager.get().getUUID()
            val playerId = TerritoryManager.get().me?.id
            val targetTerritory = TerritoryManager.get().getPrevSelTerritory()
            val targetTerritoryId = targetTerritory?.getTerritoryId()

            if (playerId == null || targetTerritoryId == null) {
                Toast.makeText(this, "Spieler oder Zielterritorium nicht verfügbar!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val result = networkClient.cheatConquer(gameId, playerId, targetTerritoryId)
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        Toast.makeText(
                            this@MapPreviewActivity,
                            "Erfolg: Territory übernommen",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MapPreviewActivity,
                            "Fehler: ${result.exceptionOrNull()?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    fun onTerritoryClicked(clickedTerritoryId: Int, fromTerritory: ITerritoryVisual) {
        val toTerritory = TerritoryManager.get().getTerritoryById(clickedTerritoryId)

        if (toTerritory == null) {
            Toast.makeText(this, "Territorium nicht gefunden!", Toast.LENGTH_SHORT).show()
            return
        }

        if (cheatModeEnabled) {
            if (cheatUsedThisRound) {
                Toast.makeText(this, "Cheat nur einmal pro Runde erlaubt!", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            val gameId = GameManager.get().getUUID()
            val playerId = TerritoryManager.get().me?.id

            if (playerId == null) {
                Toast.makeText(this, "Spieler nicht gefunden!", Toast.LENGTH_SHORT).show()
                return
            }


            CoroutineScope(Dispatchers.IO).launch {
                val result = networkClient.cheatConquer(gameId, playerId, clickedTerritoryId)
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        Toast.makeText(
                            this@MapPreviewActivity,
                            "Cheat Eroberung erfolgreich!",
                            Toast.LENGTH_SHORT
                        ).show()
                        cheatUsedThisRound = true
                    } else {
                        Toast.makeText(
                            this@MapPreviewActivity,
                            "Fehler: ${result.exceptionOrNull()?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            return
        }
    }
}
