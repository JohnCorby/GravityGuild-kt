package com.johncorby.gravityguild

import com.johncorby.gravityguild.arena.Listener
import com.johncorby.gravityguild.arena.arenaGames
import com.johncorby.gravityguild.arena.arenaWorlds
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
        for (game in ArrayList(arenaGames)) game.close()

        info("disabled")
    }
}
