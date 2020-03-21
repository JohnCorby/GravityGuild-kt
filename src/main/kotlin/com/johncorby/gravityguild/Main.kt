package com.johncorby.gravityguild

import org.bukkit.plugin.java.JavaPlugin
import org.mineacademy.gameapi.ArenaManager
import org.mineacademy.gameapi.ArenaPlugin
import org.mineacademy.gameapi.registry.ArenaRegistry

lateinit var PLUGIN: Main

class Main : JavaPlugin(), ArenaPlugin {
    override fun onEnable() {
        PLUGIN = this
    }

    override fun getPlugin() = PLUGIN

    override fun getArenas(): ArenaManager = ArenaRegistry.getArenaManager(PLUGIN)
}
