package com.johncorby.gravityguild.arena

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import com.johncorby.gravityguild.*
import com.johncorby.gravityguild.arena.ArrowTracker.startTracking
import com.johncorby.gravityguild.arena.ArrowTracker.stopTracking
import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.entity.WitherSkull
import org.bukkit.event.block.Action
import org.bukkit.event.entity.*
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent

object Listener {
    init {
        listen<PlayerJoinEvent> {
            player.warn("this plugin is actively in development!")
            player.warn("submit any bugs you find at https://github.com/johncorby/gravityguild-kt/issues")

            player.gameIn?.onJoin(player)
        }
        listen<PlayerQuitEvent> { player.gameIn?.onLeave(player) }

        listen<PlayerTeleportEvent> {
            if (from.world == to.world) return@listen
            // schedule 1 tick later so this happens after the teleport
            PLUGIN.schedule {
                arenaGames.find { to.world == it.world }?.onJoin(player)
                arenaGames.find { from.world == it.world }?.onLeave(player)
            }
        }




        listen<ProjectileLaunchEvent> {
            if (!entity.inGame) return@listen
            if (entity !is Arrow) return@listen
            if (entity.shooter !is Player) return@listen

            // no gravity
            entity.setGravity(false)
            (entity as Arrow).startTracking()
        }
        listen<ProjectileCollideEvent> {
            if (!entity.inGame) return@listen
            if (entity.shooter !is Player) return@listen

            // make it so players cant hit themselves with their own projectiles
            if (entity.shooter == collidedWith) isCancelled = true
        }
        listen<ProjectileHitEvent> {
            if (!entity.inGame) return@listen
            if (entity.shooter !is Player) return@listen
            when (entity) {
                is Arrow -> {
                    (hitEntity as? Player)?.damage(BIG_NUMBER.toDouble())
                    (entity as Arrow).stopTracking()
                    entity.remove()
                }
                is Snowball -> {
                    // death snowball
                    (hitEntity as? Player)?.damage(BIG_NUMBER.toDouble())
                    entity.world.strikeLightningEffect(entity.location)
                }
            }
        }
        listen<PlayerInteractEvent> {
            if (!player.inGame) return@listen
            if (action != Action.LEFT_CLICK_BLOCK) return@listen

            // shoot skull
            player.launchProjectile(WitherSkull::class.java, player.eyeLocation.direction)
            // cancel so player doesnt break anything
            isCancelled = true
        }


        listen<EntityDamageEvent> {
            if (!entity.inGame) return@listen
            if (entity !is Player) return@listen

            if (cause !in arrayOf(DamageCause.FALL, DamageCause.ENTITY_EXPLOSION)) return@listen
            if ((entity as Player).health - damage <= 0) return@listen
            damage = 0.0
        }
        listen<PlayerDeathEvent> {
            entity.gameIn?.let { game ->
                isCancelled = true

                keepInventory = true
                keepLevel = true

                game.broadcast(deathMessage.orNullError("death message"))
                game.broadcast("${entity.name} has ${unitize(--entity.lives, "life", "lives")} remaining")

                // todo respawn/kick
                entity.initForArena()
            }
        }

        listen<FoodLevelChangeEvent> {
            if (entity.inGame) isCancelled = true
        }
        listen<PlayerPickupExperienceEvent> {
            if (player.inGame) isCancelled = true
        }
    }
}
