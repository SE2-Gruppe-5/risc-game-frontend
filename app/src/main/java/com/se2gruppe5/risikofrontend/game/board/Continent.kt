package com.se2gruppe5.risikofrontend.game.board

import androidx.core.graphics.toColorInt
import kotlinx.serialization.Serializable


@Serializable
enum class Continent(val color: Int) {
    POWER_SUPPLY("#b5edf9".toColorInt()),
    MMC("#e8f3a8".toColorInt()),
    RAM("#87deb3".toColorInt()),
    DCON("#f0a8e1".toColorInt()),
    CPU("#e6afaf".toColorInt()),
    ESSENTIALS("#fec466".toColorInt()),
    SOUTHBRIDGE("#f5d557".toColorInt()),
    WIRELESS_MESH("#9effad".toColorInt()),
    EMBEDDED_CONTROLLER("#9b3e3e".toColorInt()),
    CMOS("#a4e5e2".toColorInt());
}