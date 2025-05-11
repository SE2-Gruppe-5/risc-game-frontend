package com.se2gruppe5.risikofrontend.network.sse.messages

import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import com.se2gruppe5.risikofrontend.network.sse.IMessage

data class ChangeTerritoryMessage(val territories: List<TerritoryRecord>) : IMessage
