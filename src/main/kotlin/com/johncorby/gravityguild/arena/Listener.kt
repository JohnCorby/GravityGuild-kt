package com.johncorby.gravityguild.arena

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import com.johncorby.gravityguild.PLUGIN
import com.johncorby.gravityguild.broadcast
import com.johncorby.gravityguild.unitize
import com.johncorby.gravityguild.warn
import hazae41.minecraft.kutils.bukkit.listen
import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.WitherSkull
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.entity.*
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
            if (entity !is Player) return@listen
            if (!entity.inArena) return@listen

            // no gravity
            entity.setGravity(false)
            // fixme preserve velocity because for some reason it slows down over time
        }
        listen<ProjectileCollideEvent> {
            if (entityType != EntityType.ARROW) return@listen
            if (!entity.inArena) return@listen

            // make it so players cant shoot themselves
            if (entity.shooter == collidedWith) isCancelled = true
        }
        listen<ProjectileHitEvent> {
            if (!entity.inArena) return@listen
            when (entityType) {
                EntityType.ARROW -> {
                    (hitEntity as? Player)?.damage(9999.0, hitEntity)
                    entity.remove()
                }
                EntityType.SNOWBALL -> {
                    // death snowball
                    (hitEntity as? Player)?.damage(9999.0, hitEntity)
                    entity.world.strikeLightningEffect(entity.location)
                }
            }
        }
        listen<PlayerInteractEvent> {
            if (action != Action.LEFT_CLICK_BLOCK) return@listen
            if (!player.inArena) return@listen

            // shoot skull
            player.launchProjectile(WitherSkull::class.java, player.location.direction)
            // cancel so player doesnt break anything
            isCancelled = true
        }


        listen<EntityDamageEvent> {
            if (entity !is Player) return@listen
            if (!entity.inArena) return@listen

            // if non-lethal, dont take damage (but still do animation/knockback)
            if ((entity as Player).health - damage > 0 && cause in arrayOf<EntityDamageEvent.DamageCause>(
                    EntityDamageEvent.DamageCause.FALL,
                    EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                )
            ) damage = 0.0
        }
        listen<PlayerDeathEvent> {
            entity.arenaIn?.let { game ->
                isCancelled = true

                entity.lives--

                game.broadcast(deathMessage!!)
                game.broadcast("${entity.name} has ${unitize(entity.lives, "life", "lives")} remaining")

                // todo respawn/kick
                entity.initForArena()
            }
        }

        listen<FoodLevelChangeEvent> {
            if (entity.inArena) isCancelled = true
        }
        listen<PlayerPickupExperienceEvent> {
            if (player.inArena) isCancelled = true
        }
    }

    private inline fun <reified T : Event> listen(crossinline callback: T.() -> Unit) =
        PLUGIN.listen(callback = callback)
}
