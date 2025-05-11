package com.se2gruppe5.risikofrontend.network.sse.messages

import com.se2gruppe5.risikofrontend.network.sse.IMessage
import java.util.UUID

data class JoinLobbyMessage(val uuid: UUID, val playerName: String, val lobbyCode: String) : IMessage
