package com.se2gruppe5.risikofrontend.game.dataclasses

data class PlayerRecord(val id: Int, val name: String, val color: Int){
    var stars = 0
    var freeTroops = 0
}