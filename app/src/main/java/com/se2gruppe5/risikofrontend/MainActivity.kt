package com.se2gruppe5.risikofrontend

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.se2gruppe5.risikofrontend.network.NetworkClient
import com.se2gruppe5.risikofrontend.network.sse.MessageType
import com.se2gruppe5.risikofrontend.network.sse.SseClientService
import com.se2gruppe5.risikofrontend.network.sse.constructServiceConnection
import com.se2gruppe5.risikofrontend.network.sse.messages.ChatMessage
import androidx.lifecycle.lifecycleScope
import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import com.se2gruppe5.risikofrontend.popup.ContinentDialog
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {
    val client = NetworkClient()
    var sseService: SseClientService? = null
    val serviceConnection = constructServiceConnection { service ->
        // Allow network calls on main thread for testing purposes
        // GitHub Actions Android emulator action with stricter policy fails otherwise
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        sseService = service
        if (service != null) {
            setupHandlers(service)
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtMessage = this.findViewById<EditText>(R.id.txtMessage)
        val button = this.findViewById<Button>(R.id.button)
        val menuButton = this.findViewById<Button>(R.id.menuButton)
        val btnShowContinents = findViewById<Button>(R.id.btnShowContinents)

            button.setOnClickListener {
                Log.i("WEBCHAT", "Sending message: " + txtMessage.text)
                lifecycleScope.launch {
                    client.sendChat(txtMessage.text.toString())
                }
                txtMessage.setText("")
            }

        btnShowContinents.setOnClickListener {
            val continentDialog = ContinentDialog()
            continentDialog.show(supportFragmentManager, "ContinentDialog")
            Log.d("MainActivity", "Kontinente Button clicked")
        }

            menuButton.setOnClickListener {
                Log.i("NAVIGATION", "Going to Menu")
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }
        }

        override fun onStart() {
            super.onStart()
            Intent(this, SseClientService::class.java).also {
                bindService(it, serviceConnection, BIND_AUTO_CREATE)
            }
        }

        override fun onStop() {
            super.onStop()
            if (sseService != null) {
                unbindService(serviceConnection)
            }
        }

        private fun setupHandlers(service: SseClientService) {
            val textView = this.findViewById<TextView>(R.id.textView)
            service.handler(MessageType.CHAT) {
                it as ChatMessage
                runOnUiThread {
                    textView.append(it.message + "\n")
                }
            }
        }

    }
