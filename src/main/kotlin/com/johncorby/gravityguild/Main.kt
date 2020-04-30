package com.johncorby.gravityguild

import com.johncorby.coreapi.Plugin
import com.johncorby.coreapi.info
import com.johncorby.gravityguild.arena.GameListener
import com.johncorby.gravityguild.arena.MAP_WORLD_SUFFIX
import com.johncorby.gravityguild.arena.WorldHelper
import com.johncorby.gravityguild.arena.games

class Main : Plugin() {
    override fun onEnable() {
        super.onEnable()

        // load map worlds since thats not done without multiverse which we're not using for user convenience
        server.worldContainer
            .list { _, name -> name.endsWith(MAP_WORLD_SUFFIX) }
            ?.forEach { WorldHelper.createOrLoad(it) }

        Config
        Data
        GameListener
        Command

        info("enabled")
    }

    override fun onDisable() {
        super.onDisable()

        // clone to prevent exception
        for (game in games.toList()) game.close()

        info("disabled")
    }
}
