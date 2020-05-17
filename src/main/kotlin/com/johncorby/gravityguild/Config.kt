package com.johncorby.gravityguild

import com.johncorby.coreapi.ConfigFile
import org.bukkit.Location

object Config : ConfigFile() {
    val MIN_PLAYERS by field("players.min", 2)
    val MAX_PLAYERS by field("players.max", 10)
    val LIVES by field("players.lives", 3)

    val COUNTDOWN_INTERVALS by field("countdown-intervals", listOf(20, 10, 5, 4, 3, 2, 1))
    val COOLDOWN_TIME by field("cooldown-time", 5)
    val WIN_WAIT_TIME by field("win-wait-time", 5)
}


object Data : ConfigFile("data.yml") {
    var lobby by field<Location?>("lobby")
}

