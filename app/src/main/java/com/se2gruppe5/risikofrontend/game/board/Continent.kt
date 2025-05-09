package com.se2gruppe5.risikofrontend.game.board

import kotlinx.serialization.Serializable

@Serializable
enum class Continent {
    POWER_SUPPLY,
    MMC,
    RAM,
    DCON,
    CPU,
    ESSENTIALS,
    SOUTHBRIDGE,
    WIRELESS_MESH,
    EMBEDDED_CONTROLLER,
    CMOS;
}