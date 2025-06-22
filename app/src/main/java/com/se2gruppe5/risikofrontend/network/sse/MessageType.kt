package com.se2gruppe5.risikofrontend.network.sse

import com.se2gruppe5.risikofrontend.game.vibrateonterritoryloss.VibrateClientMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.ChangeTerritoryMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.ChatMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.GameStartMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.JoinLobbyMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.LeaveLobbyMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.SetUuidMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.UpdatePhaseMessage
import com.se2gruppe5.risikofrontend.network.sse.messages.UpdatePlayersMessage
import kotlin.reflect.KClass

enum class MessageType(val messageClass: KClass<out IMessage>) {
    SET_UUID(SetUuidMessage::class),
    CHAT(ChatMessage::class),
    JOIN_LOBBY(JoinLobbyMessage::class),
    LEAVE_LOBBY(LeaveLobbyMessage::class),
    START_GAME(GameStartMessage::class),
    UPDATE_PHASE(UpdatePhaseMessage::class),
    UPDATE_PLAYERS(UpdatePlayersMessage::class),
    UPDATE_TERRITORIES(ChangeTerritoryMessage::class),
    VIBRATE_CLIENT(VibrateClientMessage::class);
}

fun getMessageType(type: String): MessageType? {
    return try {
        MessageType.valueOf(type)
    } catch (_: Exception) {
        null
    }
}
