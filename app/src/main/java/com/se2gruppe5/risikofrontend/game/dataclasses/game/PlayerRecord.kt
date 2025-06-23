package com.se2gruppe5.risikofrontend.game.dataclasses.game

import java.io.Serializable
import java.util.UUID

data class PlayerRecord(val id: UUID, val name: String, val color: Int): Serializable {
    var cards = mutableListOf<CardRecord>()
    var capturedTerritory = false
    var freeTroops = 25
    var isCurrentTurn = false
    var isDead = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayerRecord

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
