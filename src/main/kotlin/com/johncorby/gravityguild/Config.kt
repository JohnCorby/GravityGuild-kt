package com.johncorby.gravityguild

import com.johncorby.coreapi.ConfigFile
import org.bukkit.Location

object Config : ConfigFile() {
    val MIN_PLAYERS by Field("players.min", 2)
    val MAX_PLAYERS by Field("players.max", 10)
    val LIVES by Field("players.lives", 3)

    val COUNTDOWN_INTERVALS by Field("countdown-intervals", listOf(20, 10, 5, 4, 3, 2, 1))
    val COOLDOWN_TIME by Field("cooldown-time", 5)
    val WIN_WAIT_TIME by Field("win-wait-time", 5)
}


object Data : ConfigFile("data.yml") {
    var lobby by Field<Location?>("lobby")
}

