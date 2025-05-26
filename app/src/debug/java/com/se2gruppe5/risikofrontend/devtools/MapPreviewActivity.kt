package com.se2gruppe5.risikofrontend.devtools

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.game.board.BoardVisualGeneratorAndroid

class MapPreviewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.devtools_map_preview)

        BoardVisualGeneratorAndroid.initTerritoryViews(this, true)
    }
}
