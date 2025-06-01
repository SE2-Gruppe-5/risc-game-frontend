package com.se2gruppe5.risikofrontend.game.vibrateonterritoryloss

import com.se2gruppe5.risikofrontend.network.sse.IMessage
import java.util.UUID

data class VibrateClientMessage(
    val playerId: UUID,
    val durationMs: Int,
    val intensity: Int
) : IMessage
