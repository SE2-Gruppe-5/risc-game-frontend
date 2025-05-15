package com.se2gruppe5.risikofrontend.game.board

import kotlinx.serialization.Serializable


@Serializable
enum class Continent(val color: String) {
    POWER_SUPPLY("#b5edf9"),
    MMC("#e8f3a8"),
    RAM("#87deb3"),
    DCON("#f0a8e1"),
    CPU("#e6afaf"),
    ESSENTIALS("#fec466"),
    SOUTHBRIDGE("#f5d557"),
    WIRELESS_MESH("#9effad"),
    EMBEDDED_CONTROLLER("#9b3e3e"),
    CMOS("#a4e5e2");
}