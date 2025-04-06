package com.se2gruppe5.risikofrontend.lobby

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.se2gruppe5.risikofrontend.R
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity

class CreateLobbyActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.createlobby)

        val backBtn = this.findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener({
            Log.i("NAVIGATION", "Sending message: Quit lobby")
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        })

        val createBtn = this.findViewById<Button>(R.id.createLobbyBtn)
        val nameInput = findViewById<EditText>(R.id.name_input)
        createBtn.setOnClickListener({
            val name = nameInput.text.toString()
            Log.i("NAVIGATION", "Sending message: Create lobby")
            val intent = Intent(this, LobbyActivity::class.java)
            intent.putExtra("PLAYER_NAME", name)
            startActivity(intent)
        })

    }
}
