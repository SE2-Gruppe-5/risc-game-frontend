package com.se2gruppe5.risikofrontend.network.sse.messages

import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.network.sse.IMessage
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class GameStartMessage(val gameId: UUID, val players : HashMap<UUID, PlayerRecord>) : IMessage
