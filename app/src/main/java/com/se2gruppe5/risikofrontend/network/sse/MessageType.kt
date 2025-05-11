package com.se2gruppe5.risikofrontend.network.sse

import com.google.gson.JsonObject
import com.se2gruppe5.risikofrontend.network.sse.messages.ChatMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.GameStartMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.JoinLobbyMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.LeaveLobbyMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.SetUuidMessage
import java.util.UUID
import java.util.function.Function

enum class MessageType(private val deserializer: Function<JsonObject, IMessage?>) {
    SET_UUID(
        Function { data: JsonObject ->
            SetUuidMessage(UUID.fromString(data.get("uuid")?.asString))
        }
    ),
    CHAT(
        Function { data: JsonObject ->
            ChatMessage(data.get("message")?.asString!!)
        }
    ),
    JOIN_LOBBY(
        Function { data: JsonObject ->
            JoinLobbyMessage(
                UUID.fromString(data.get("uuid")?.asString),
                data.get("playerName")?.asString!!,
                data.get("lobbyCode")?.asString!!
            )
        }
    ),
    LEAVE_LOBBY(
        Function { data: JsonObject ->
            LeaveLobbyMessage(UUID.fromString(data.get("uuid")?.asString))
        }
    ),
    GAME_START(
        Function { data: JsonObject ->
            GameStartMessage(UUID.fromString(data.get("gameId")?.asString))
        }
    ),
    UPDATE_PHASE,
    UPDATE_PLAYERS,
    UPDATE_TERRITORIES;

    fun deserialize(data: JsonObject): IMessage? {
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