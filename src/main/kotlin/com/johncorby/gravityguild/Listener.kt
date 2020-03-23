package com.johncorby.gravityguild

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

object Listener : Listener {
    init {
        PLUGIN.server.pluginManager.registerEvents(Listener, PLUGIN)
    }
}
