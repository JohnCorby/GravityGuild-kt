package com.johncorby.gravityguild

import com.johncorby.coreapi.DelegateConfigFile
import org.bukkit.Location

object Config : DelegateConfigFile() {
    val MIN_PLAYERS by Key("min-players", 2)
    val MAX_PLAYERS by Key("max-players", 10)
    val LIVES by Key("lives", 3)
}


object Data : DelegateConfigFile("data.yml") {
    internal val NULL_LOCATION = Location(null, 0.0, 0.0, 0.0)

    var lobby by Key("lobby", NULL_LOCATION)
}

