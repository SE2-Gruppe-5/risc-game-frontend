package com.se2gruppe5.risikofrontend.game

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.Popup.ContinentDialog
import com.se2gruppe5.risikofrontend.R

class GameActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.game)

        val showContinentButton: Button = findViewById(R.id.btn_show_continents)
        showContinentButton.setOnClickListener {
            showContinentDialog()
        }
    }
    fun showContinentDialog() {
        val dialog = ContinentDialog()
        dialog.show(supportFragmentManager, "ContinentDialog")
    }
}