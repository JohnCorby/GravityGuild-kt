package com.johncorby.gravityguild

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

lateinit var PLUGIN: Main

class Main : JavaPlugin() {
    override fun onEnable() {
        PLUGIN = this

        Command
        Listener
        Config

        Bukkit.getConsoleSender().info("enabled")
    }

    override fun onDisable() {
        for (arena in arenas.values)
            for (instance in arena.instances)
                instance.close()

        Bukkit.getConsoleSender().info("disabled")
    }
}
