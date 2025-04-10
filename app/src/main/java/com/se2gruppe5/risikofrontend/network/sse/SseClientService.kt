package com.se2gruppe5.risikofrontend.network.sse

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.launchdarkly.eventsource.EventSource
import com.se2gruppe5.risikofrontend.Constants
import com.se2gruppe5.risikofrontend.network.NetworkClient
import com.se2gruppe5.risikofrontend.network.sse.messages.SetUuidMessage
import java.net.URI
import java.time.Duration
import java.util.UUID
import java.util.function.Consumer

class SseClientService : Service() {
    companion object {
        var uuid: UUID? = null
    }

    private val binder = LocalBinder()
    private val eventHandler = SseEventHandler(this)
    private var eventSource: EventSource? = null

    private val handlers: HashMap<MessageType, Consumer<Message>> = HashMap()

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        setupDefaultHandlers()
        connectEventSource()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventSource?.close()
    }

    private fun setupDefaultHandlers() {
        handler(MessageType.SET_UUID) { it as SetUuidMessage
            uuid = it.uuid
        }
    }

    private fun connectEventSource() {
        val url = if (uuid == null) {
            Constants.Companion.SSE_URL
        } else {
            Constants.Companion.SSE_URL_REJOIN.replace("{id}", uuid.toString())
        }
        eventSource = EventSource.Builder(eventHandler, URI.create(Constants.Companion.HOST + url))
            .connectTimeout(Duration.ofSeconds(10))
            .backoffResetThreshold(Duration.ofSeconds(10))
            .build()
        eventSource!!.start()
    }

    fun handleIncomingMessage(type: MessageType, message: Message) {
        handlers.get(type)?.accept(message)
    }

    fun handler(type: MessageType, handler: Consumer<Message>) {
        handlers.put(type, handler)
    }

    inner class LocalBinder : Binder() {
        fun getService(): SseClientService {
            return this@SseClientService
        }
    }
}

fun constructServiceConnection(setService: Consumer<SseClientService?>): ServiceConnection {
    return object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SseClientService.LocalBinder
            setService.accept(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            setService.accept(null)
        }
    }
}