package com.se2gruppe5.risikofrontend.network.sse.messages

import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.network.sse.IMessage
import java.util.UUID

data class UpdatePlayersMessage(val players: HashMap<UUID, PlayerRecord>) : IMessage
