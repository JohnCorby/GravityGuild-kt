package com.johncorby.gravityguild

import com.johncorby.gravityguild.arena.Listener
import com.johncorby.gravityguild.arena.arenaGames
import com.johncorby.gravityguild.arena.arenaWorlds
import org.bukkit.plugin.java.JavaPlugin

lateinit var PLUGIN: Main

/*
todo
    [this models hypixel and is the alternative to choosing to world/instance somehow yourself]
    instead of arenas being stored with bases and instances,
    we'll simply have arena worlds be saved (probably keep track of them via a list of worlds).
    then, when a player initiates a join (probably by clicking a sign),
    if there already exists running games, we place the player into the one with the most players already in it.
    OR
    if all games are full,
    we simply create an arena game object that clones one of the arena worlds to use as an instance.
    remember that if all players leave a game, it will close and therefore delete itself
 */
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
        for (game in arenaGames) game.close()

        info("disabled")
    }
}
