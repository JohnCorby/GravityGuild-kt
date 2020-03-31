package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.PLUGIN
import hazae41.minecraft.kutils.bukkit.listen
import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object Listener : Listener {
    init {
        PLUGIN.server.pluginManager.registerEvents(
            Listener,
            PLUGIN
        )

        PLUGIN.listen<PlayerJoinEvent> { it.player.arenaIn?.onJoin(it.player) }
        PLUGIN.listen<PlayerQuitEvent> { it.player.arenaIn?.onLeave(it.player) }

        PLUGIN.listen<PlayerTeleportEvent> { e ->
            if (e.from.world == e.to.world) return@listen
            instances.forEach {
                when {
                    e.to.world == it.world -> PLUGIN.schedule { it.onJoin(e.player) }
                    e.from.world == it.world -> it.onLeave(e.player)
                }
            }
        }
    }
}
