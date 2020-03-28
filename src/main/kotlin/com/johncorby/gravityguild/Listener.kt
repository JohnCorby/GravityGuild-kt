package com.johncorby.gravityguild

import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object Listener : Listener {
    init {
        PLUGIN.server.pluginManager.registerEvents(Listener, PLUGIN)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) = event.player.arenaIn?.onJoin(event.player)

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) = event.player.arenaIn?.onLeave(event.player)

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        if (event.from.world == event.to.world) return
        instances.forEach {
            when {
                event.to.world == it.world -> PLUGIN.schedule { it.onJoin(event.player) }
                event.from.world == it.world -> it.onLeave(event.player)
            }
        }
    }
}
