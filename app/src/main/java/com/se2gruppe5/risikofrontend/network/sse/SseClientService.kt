package com.se2gruppe5.risikofrontend.network.sse

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.launchdarkly.eventsource.EventSource
import com.se2gruppe5.risikofrontend.Constants
import com.se2gruppe5.risikofrontend.network.sse.messages.SetUuidMessage
import java.net.URI
import java.time.Duration
import java.util.Comparator
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer

class SseClientService : Service() {
    companion object {
        var uuid: UUID? = null
    }

    private val binder = LocalBinder()
    private val eventHandler = SseEventHandler(this)
    private var eventSource: EventSource? = null

    private var lock: ReentrantLock = ReentrantLock(true)
    private var replay: HashMap<Long, ReplayMessage> = HashMap()
    private var replaying = true

    private val handlers: HashMap<MessageType, Consumer<IMessage>> = HashMap()

    override fun onBind(p0: Intent?): IBinder {
        setupDefaultHandlers()
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
        lock.lock()
        try {
            replaying = true
            handlers.clear()
            handler(MessageType.SET_UUID) { it as SetUuidMessage
                uuid = it.uuid
            }
        } finally {
            lock.unlock()
        }
    }

    private fun connectEventSource() {
        val url = if (uuid == null) {
            Constants.SSE_URL
        } else {
            Constants.SSE_URL_REJOIN.replace("{id}", uuid.toString())
        }
        eventSource = EventSource.Builder(eventHandler, URI.create(Constants.HOST + url))
            .connectTimeout(Duration.ofSeconds(10))
            .backoffResetThreshold(Duration.ofSeconds(10))
            .build()
        eventSource!!.start()
    }

    fun replayMessages() {
        lock.lock()
        try {
            Log.d("SSE_REPLAY", "Replaying ${replay.size} messages")
            replay.entries.stream()
                .sorted(Comparator.comparing { it.key })
                .forEachOrdered {
                    val message = it.value
                    handlers[message.type]?.accept(message.message)
                }
            replay.clear()
            replaying = false
        } finally {
            lock.unlock()
        }
    }

    fun handleIncomingMessage(type: MessageType, message: IMessage) {
        lock.lock()
        try {
            if (replaying) {
                Log.d("SSE_REPLAY", "Message type $type not handled, saving to replay")
                replay[System.nanoTime()] = ReplayMessage(type, message)
            } else {
                handlers[type]?.accept(message)
            }
        } finally {
            lock.unlock()
        }
    }

    fun handler(type: MessageType, handler: Consumer<IMessage>) {
        handlers[type] = handler
    }

    inner class LocalBinder : Binder() {
        fun getService(): SseClientService {
            return this@SseClientService
        }
    }

    private data class ReplayMessage(val type: MessageType, val message: IMessage)
}

fun constructServiceConnection(setService: Consumer<SseClientService?>): ServiceConnection {
    return object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SseClientService.LocalBinder
            val service = binder.getService()
            setService.accept(service)
            service.replayMessages()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            setService.accept(null)
        }
    }
}
