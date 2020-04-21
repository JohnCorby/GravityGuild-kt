package com.johncorby.gravityguild

import hazae41.minecraft.kutils.bukkit.ConfigFile
import hazae41.minecraft.kutils.bukkit.PluginConfigFile
import hazae41.minecraft.kutils.bukkit.init
import hazae41.minecraft.kutils.get

object Config : PluginConfigFile("config") {
    var debug by boolean("debug")
    var minPlayers = 2 // todo
    var maxPlayers by int("max-players")
    var lives by int("lives")

    init {
        PLUGIN.init(this)
    }
}

object Data : ConfigFile(PLUGIN.dataFolder["data.yml"]) {
    var lobby by location("lobby")
}

