package com.se2gruppe5.risikofrontend.game.dataclasses

import java.util.UUID

data class PlayerRecord(val id: UUID, val name: String, val color: Int){
    var cards = mutableListOf<CardRecord>();
    var capturedTerritory = false;
    var freeTroops = 0
}