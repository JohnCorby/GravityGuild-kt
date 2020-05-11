package com.johncorby.gravityguild

import com.johncorby.coreapi.DelegateConfigFile
import org.bukkit.Location

object Config : DelegateConfigFile() {
    val MIN_PLAYERS by Key("players.min", 2)
    val MAX_PLAYERS by Key("players.max", 10)
    val LIVES by Key("players.lives", 3)

    val COUNTDOWN_INTERVALS by Key("countdown-intervals", listOf(20, 10, 5, 4, 3, 2, 1))
    val COOLDOWN_TIME by Key("cooldown-time", 5)
    val WIN_WAIT_TIME by Key("win-wait-time", 5)
}


object Data : DelegateConfigFile("data.yml") {
    internal val NULL_LOCATION = Location(null, 0.0, 0.0, 0.0)

    var lobby by Key("lobby", NULL_LOCATION)
}

