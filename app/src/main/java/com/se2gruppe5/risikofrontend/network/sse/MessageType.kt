package com.se2gruppe5.risikofrontend.network.sse

import com.google.gson.JsonObject
import com.se2gruppe5.risikofrontend.network.sse.messages.ChatMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.SetUuidMessage
import java.util.UUID
import java.util.function.Function

enum class MessageType(private val deserializer: Function<JsonObject, Message?>) {
    SET_UUID(
        Function { data: JsonObject ->
            SetUuidMessage(UUID.fromString(data.get("uuid")?.asString))
        }
    ),
    CHAT(
        Function { data: JsonObject ->
            ChatMessage(data.get("message")?.asString!!)
        }
    );

    fun deserialize(data: JsonObject): Message? {
        return deserializer.apply(data)
    }
}

fun getMessageType(type: String): MessageType? {
    return try {
        MessageType.valueOf(type)
    } catch (_: Exception) {
        null
    }
}