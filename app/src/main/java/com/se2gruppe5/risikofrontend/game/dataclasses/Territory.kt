package com.se2gruppe5.risikofrontend.game.dataclasses

import com.se2gruppe5.risikofrontend.game.territoryIO.ITerritoryUIWrapper

data class Territory (val id: Int, val visualization: ITerritoryUIWrapper, var owner: Player? = null)