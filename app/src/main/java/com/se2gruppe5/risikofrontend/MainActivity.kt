package com.se2gruppe5.risikofrontend

import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import com.se2gruppe5.risikofrontend.network.NetworkClient
import com.se2gruppe5.risikofrontend.startmenu.MenuActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.ConnectException

class MainActivity : AppCompatActivity() {
    val client = NetworkClient()

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

        val textView = this.findViewById<TextView>(R.id.textView)
        val txtMessage = this.findViewById<EditText>(R.id.txtMessage)
        val button = this.findViewById<Button>(R.id.button)
        val menuButton = this.findViewById<Button>(R.id.menuButton)

        run {
            SSEClient().init(AppendingHandler(textView))
        }

        button.setOnClickListener({
            Log.i("WEBCHAT", "Sending message: " + txtMessage.text)
            lifecycleScope.launch {
                client.sendChat(txtMessage.text.toString())
            }
            txtMessage.setText("")
        })
        menuButton.setOnClickListener({
            Log.i("NAVIGATION", "Going to Menu")
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

class AppendingHandler(private val textView: TextView) : EventHandler {
    override fun onOpen() {
        Log.i("SSE-LIFECYCLE", "onOpen")
    }

    override fun onClosed() {
        Log.i("SSE-LIFECYCLE", "onClosed")
    }

    override fun onMessage(event: String?, messageEvent: MessageEvent?) {
        Log.i("SSE-LIFECYCLE", "Event: " + event + ": " + messageEvent?.data)
        messageEvent?.let {
            this.textView.append("\n" + it.data)
        }
    }

    override fun onComment(comment: String?) {
        Log.i("SSE-LIFECYCLE", "onComment: " + comment)
    }

    override fun onError(t: Throwable?) {
        Log.e("SSE-LIFECYCLE", "onError", t)
    }

}
