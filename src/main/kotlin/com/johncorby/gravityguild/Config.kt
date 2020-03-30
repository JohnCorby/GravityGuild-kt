package com.johncorby.gravityguild

import com.johncorby.gravityguild.arena.ArenaBase
import hazae41.minecraft.kutils.bukkit.ConfigFile
import hazae41.minecraft.kutils.bukkit.PluginConfigFile
import hazae41.minecraft.kutils.bukkit.init
import hazae41.minecraft.kutils.bukkit.keys
import hazae41.minecraft.kutils.get

object Options : PluginConfigFile("config") {
    var debug by boolean("debug")

    init {
        PLUGIN.init(this)
    }
}

object Data : ConfigFile(PLUGIN.dataFolder["data.yml"]) {
    var lobby by location("lobby")
    var arenas by section("arenas")

    init {
        arenas?.keys?.forEach {
            ArenaBase(it)
        }
    }
}

