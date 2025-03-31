package com.se2gruppe5.risikofrontend

import android.annotation.SuppressLint
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {
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

        run {
            SSEClient().init(AppendingHandler(textView))
        }

        button.setOnClickListener({
            Log.i("WEBCHAT", "Sending message: " + txtMessage.text)
            lifecycleScope.launch {
                sendChat(txtMessage.text.toString())
            }
            txtMessage.setText("")
        })
    }

    private suspend fun sendChat(message: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.0.91:8080/chat/send")
            .post(MultipartBody.Builder()
                .addFormDataPart("message", message)
                .build())
            .build()
        val call = client.newCall(request)
        return withContext(Dispatchers.IO) {
            Log.i("WEBCHAT", "Executing request")
            call.execute()
            Log.i("WEBCHAT", "Executed request")
        }
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