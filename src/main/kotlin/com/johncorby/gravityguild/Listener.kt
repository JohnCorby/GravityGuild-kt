package com.johncorby.gravityguild

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import org.bukkit.Bukkit
import org.bukkit.event.Listener

object Listener : Listener {
    init {
        Bukkit.getServer().pluginManager.registerEvents(Listener, PLUGIN)
    }

    fun onEntityCreate(event: EntityAddToWorldEvent) = event.entity.also { it.arenaIn?.entities?.add(it) }
    fun onEntityDestroy(event: EntityRemoveFromWorldEvent) = event.entity.also { it.arenaIn?.entities?.remove(it) }
}
