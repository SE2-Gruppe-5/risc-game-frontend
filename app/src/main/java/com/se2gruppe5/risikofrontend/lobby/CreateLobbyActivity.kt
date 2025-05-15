package com.se2gruppe5.risikofrontend.lobby

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.network.NetworkClient
import kotlinx.coroutines.runBlocking

class CreateLobbyActivity :AppCompatActivity() {
    val client = NetworkClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()
        setContentView(R.layout.createlobby)

        val backBtn = this.findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener({
            Log.i("NAVIGATION", "Quit lobby")
            finish()
        })

        val createBtn = this.findViewById<Button>(R.id.createLobbyBtn)
        val nameInput = findViewById<EditText>(R.id.name_input)
        createBtn.setOnClickListener({
            val name = nameInput.text.toString()
            Log.i("NAVIGATION", "Create lobby")
            var code = createLobby()
            if (code == null) {
                Log.e("NAVIGATION", "Error creating lobby")
                Toast.makeText(this, "Error creating lobby", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, LobbyActivity::class.java)
            intent.putExtra("PLAYER_NAME", name)
            intent.putExtra("LOBBY_CODE", code)
            startActivity(intent)
        })

    }

    private fun createLobby(): String? {
        return runBlocking {
            return@runBlocking client.createLobby().toString()
        }
    }
}
