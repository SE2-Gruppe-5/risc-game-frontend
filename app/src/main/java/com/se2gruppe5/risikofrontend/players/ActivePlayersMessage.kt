package com.se2gruppe5.risikofrontend.players

import com.se2gruppe5.risikofrontend.network.sse.IMessage

data class ActivePlayersMessage(
    val players: List<String>
) : IMessage
