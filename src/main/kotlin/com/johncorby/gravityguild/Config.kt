package com.johncorby.gravityguild

import com.johncorby.coreapi.PLUGIN
import hazae41.minecraft.kutils.bukkit.ConfigFile
import hazae41.minecraft.kutils.bukkit.PluginConfigFile
import hazae41.minecraft.kutils.bukkit.init
import hazae41.minecraft.kutils.get

object Config : PluginConfigFile("config") {
    val MIN_PLAYERS by int("min-players")
    val MAX_PLAYERS by int("max-players")
    val LIVES by int("lives")

    init {
        PLUGIN.init(this)
    }
}

object Data : ConfigFile(PLUGIN.dataFolder["data.yml"]) {
    var lobby by location("lobby")
}

