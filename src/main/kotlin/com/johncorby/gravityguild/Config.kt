package com.johncorby.gravityguild

import com.johncorby.coreapi.ConfigFile
import org.bukkit.Location

object Config : ConfigFile() {
    val MIN_PLAYERS by Key("min-players", 2)
    val MAX_PLAYERS by Key("max-players", 10)
    val LIVES by Key("lives", 3)
}

internal val NULL_LOCATION = Location(null, 0.0, 0.0, 0.0)

object Data : ConfigFile("data.yml") {
    var lobby by Key("lobby", NULL_LOCATION)
}

