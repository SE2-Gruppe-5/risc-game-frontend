package com.se2gruppe5.risikofrontend.network.sse.messages

import com.se2gruppe5.risikofrontend.game.dataclasses.game.TerritoryRecord
import com.se2gruppe5.risikofrontend.network.sse.IMessage

data class AttackTerritoryMessage(val from: TerritoryRecord, val target: TerritoryRecord, val troops: Int): IMessage {}
