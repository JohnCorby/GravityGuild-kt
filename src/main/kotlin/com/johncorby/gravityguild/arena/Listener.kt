package com.johncorby.gravityguild.arena

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.johncorby.gravityguild.PLUGIN
import hazae41.minecraft.kutils.bukkit.listen
import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.WitherSkull
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object Listener : Listener {
    init {
        PLUGIN.server.pluginManager.registerEvents(
            Listener,
            PLUGIN
        )

        listen<PlayerJoinEvent> { player.arenaIn?.onJoin(player) }
        listen<PlayerQuitEvent> { player.arenaIn?.onLeave(player) }

        listen<PlayerTeleportEvent> {
            if (from.world == to.world) return@listen
            instances.forEach {
                // trigger proper callbacks
                when {
                    to.world == it.world -> PLUGIN.schedule { it.onJoin(player) }
                    from.world == it.world -> it.onLeave(player)
                }
            }
        }




        listen<ProjectileLaunchEvent> {
            if (entityType != EntityType.ARROW) return@listen
            if (!entity.inArena) return@listen

            // no gravity
            entity.setGravity(false)
            // todo preserve velocity because for some reason it slows down over time
        }
        listen<ProjectileCollideEvent> {
            if (entityType != EntityType.ARROW) return@listen
            if (!entity.inArena) return@listen

            // make it so players cant shoot themselves
            if (entity.shooter == collidedWith) isCancelled = true
        }
        listen<ProjectileHitEvent> {
            if (entityType != EntityType.ARROW) return@listen
            if (!entity.inArena) return@listen

            // remove arrow on hit
            entity.remove()
        }


        listen<PlayerInteractEvent> {
            if (!player.inArena) return@listen
            if (action !in arrayOf(Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR)) return@listen

            // shoot skull
            player.launchProjectile(WitherSkull::class.java, player.location.direction)
            // cancel so player doesnt break anything
            isCancelled = true
        }
        listen<PlayerDeathEvent> {
            if (!entity.inArena) return@listen

            // reset damage
            entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            // todo message, life, respawn/kick, etc
        }
    }

    private inline fun <reified T : Event> listen(crossinline callback: T.() -> Unit) =
        PLUGIN.listen(callback = callback)
}
