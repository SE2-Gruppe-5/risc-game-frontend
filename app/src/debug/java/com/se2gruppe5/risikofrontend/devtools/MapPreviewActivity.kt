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
import com.se2gruppe5.risikofrontend.network.NetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MapPreviewActivity: AppCompatActivity() {
    public var cheatModeEnabled = false
    private val networkClient = NetworkClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.devtools_map_preview)

        cheatModeEnabled = true

        BoardVisualGeneratorAndroid.initTerritoryViews(this, true)

        setupCheatButton()
    }
    fun setupCheatButton() {
        val cheatButton = findViewById<Button>(R.id.cheatButton)
        cheatButton.visibility = if (cheatModeEnabled) View.VISIBLE else View.GONE

        cheatButton.setOnClickListener {
            val gameId: UUID = UUID.fromString("game-uuid-hier")
            val playerId: UUID = UUID.fromString("player-uuid-hier")
            val targetTerritoryId = 2 // Testweise statisch

            CoroutineScope(Dispatchers.IO).launch {
                val result = networkClient.cheatConquer(gameId, playerId, targetTerritoryId)
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        Toast.makeText(this@MapPreviewActivity, "Erfolg: Territory übernommen", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MapPreviewActivity, "Fehler: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        }
}
