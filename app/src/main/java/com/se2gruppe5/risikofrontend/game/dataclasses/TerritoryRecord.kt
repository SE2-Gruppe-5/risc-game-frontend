package com.se2gruppe5.risikofrontend.game.dataclasses

import java.util.UUID

data class TerritoryRecord (val id: Int, var stat: Int){
    var owner: PlayerRecord? = null
}