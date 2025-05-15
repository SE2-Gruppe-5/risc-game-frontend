package com.se2gruppe5.risikofrontend.game.enums

import kotlinx.serialization.Serializable


@Serializable
enum class Continent(val fullName: String, val color: String, val regions: Int, val bonus: Int) {
    POWER_SUPPLY("Power Supply", "#b5edf9", 6, 2),
    MMC("MMC", "#e8f3a8", 2, 2),
    RAM("RAM", "#87deb3", 10, 2),
    DCON("DCON", "#f0a8e1", 5, 2),
    CPU("CPU", "#e6afaf", 6, 2),
    ESSENTIALS("Essentials", "#fec466", 7, 2),
    SOUTHBRIDGE("Southbridge", "#f5d557", 4, 2),
    WIRELESS_MESH("Wireless Mesh", "#9effad", 8, 2),
    EMBEDDED_CONTROLLER("Embedded Controller", "#9b3e3e", 7, 2),
    CMOS("CMOS", "#a4e5e2", 5, 2);
}
