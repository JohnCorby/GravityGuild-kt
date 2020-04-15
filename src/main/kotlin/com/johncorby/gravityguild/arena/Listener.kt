package com.johncorby.gravityguild.arena

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.johncorby.gravityguild.PLUGIN
import com.johncorby.gravityguild.info
import com.johncorby.gravityguild.warn
import hazae41.minecraft.kutils.bukkit.listen
import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.WitherSkull
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object Listener {
    init {
        listen<PlayerJoinEvent> {
            player.warn("this plugin is actively in development!")
            player.warn("submit any bugs you find at https://github.com/johncorby/gravityguild-kt/issues")
            player.arenaIn?.onJoin(player)
        }
        listen<PlayerQuitEvent> { player.arenaIn?.onLeave(player) }

        listen<PlayerTeleportEvent> {
            if (from.world == to.world) return@listen
            // schedule 1 tick later so this happens after the teleport
            PLUGIN.schedule {
                arenaGames.find { to.world == it.world }?.onJoin(player)
                arenaGames.find { from.world == it.world }?.onLeave(player)
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
            // todo this doesnt actually work. it shows you the death screen but also resets your health bruh
            if (!entity.inArena) return@listen

            // reset damage instead of cancelling so hit animation still plays
            entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            // todo message, life, respawn/kick, etc
        }
    }

    private inline fun <reified T : Event> listen(crossinline callback: T.() -> Unit) =
        PLUGIN.listen(callback = callback)
}
