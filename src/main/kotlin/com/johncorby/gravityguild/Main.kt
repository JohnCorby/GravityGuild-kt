package com.johncorby.gravityguild

import org.bukkit.plugin.java.JavaPlugin

lateinit var PLUGIN: Main

class Main : JavaPlugin() {
    override fun onEnable() {
        PLUGIN = this

        server.pluginManager.registerEvents(Listener, this)
    }
}
