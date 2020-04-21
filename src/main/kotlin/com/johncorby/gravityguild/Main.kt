package com.johncorby.gravityguild

import com.johncorby.gravityguild.arena.Listener
import com.johncorby.gravityguild.arena.MAP_WORLD_SUFFIX
import com.johncorby.gravityguild.arena.WorldHelper
import com.johncorby.gravityguild.arena.games
import org.bukkit.plugin.java.JavaPlugin

lateinit var PLUGIN: Main

class Main : JavaPlugin() {
    override fun onEnable() {
        PLUGIN = this

        // load map worlds since thats not done without multiverse which we're not using for user convenience
        server.worldContainer
            .list { _, name -> name.endsWith(MAP_WORLD_SUFFIX) }
            ?.forEach { WorldHelper.createOrLoad(it) }

        Config
        Data
        Listener
        Command

        info("enabled")
    }

    override fun onDisable() {
        // clone to prevent exception
        for (game in games.toList()) game.close()

        info("disabled")
    }
}
