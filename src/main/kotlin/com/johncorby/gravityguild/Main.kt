package com.johncorby.gravityguild

import com.johncorby.gravityguild.arena.Listener
import com.johncorby.gravityguild.arena.arenas
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

lateinit var PLUGIN: Main

/*
todo
    [this models hypixel and is the alternative to choosing to world/instance somehow yourself]
    instead of arenas being stored with bases and instances,
    we'll simply have arena worlds be saved (and maybe keep track of them via a list of worlds).
    then, when a player initiates a joins (probably by clicking a sign),
    we simply create an arena object that clones one of the arena worlds to use as an instance.
    OR
    if there already exists running games, we place the player into the one with the most players already in it.
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
        for (arena in arenas.values)
            for (instance in arena.instances)
                instance.close()

        info("disabled")
    }
}
