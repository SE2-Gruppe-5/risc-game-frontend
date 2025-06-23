package com.se2gruppe5.risikofrontend.network.sse.messages

import com.se2gruppe5.risikofrontend.network.sse.IMessage
import java.util.UUID

data class AccuseCheatingMessage(val gameId: UUID, val accusedPlayerUUID: UUID) : IMessage
