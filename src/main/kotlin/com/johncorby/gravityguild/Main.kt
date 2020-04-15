package com.johncorby.gravityguild

import com.johncorby.gravityguild.arena.Listener
import com.johncorby.gravityguild.arena.arenaGames
import org.bukkit.plugin.java.JavaPlugin

lateinit var PLUGIN: Main

class Main : JavaPlugin() {
    override fun onEnable() {
        PLUGIN = this

        Options
        Data
        Listener
        Command

        info("enabled")
    }

    override fun onDisable() {
        // clone to prevent exception
        for (game in arenaGames.toList()) game.close()

        info("disabled")
    }
}
