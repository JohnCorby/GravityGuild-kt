package com.johncorby.gravityguild.arena

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import com.johncorby.gravityguild.*
import hazae41.minecraft.kutils.bukkit.listen
import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.WitherSkull
import org.bukkit.event.Event
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
            if (entityType != EntityType.ARROW) return@listen
            if (entity.shooter !is Player) return@listen
            if (!entity.inGame) return@listen

            // no gravity
            entity.setGravity(false)
            // fixme preserve velocity because for some reason it slows down over time
        }
        listen<ProjectileCollideEvent> {
            if (entity.shooter !is Player) return@listen
            if (!entity.inGame) return@listen

            // make it so players cant shoot themselves
            if (entity.shooter == collidedWith) isCancelled = true
        }
        listen<ProjectileHitEvent> {
            if (!entity.inGame) return@listen
            if (entity.shooter !is Player) return@listen
            when (entityType) {
                EntityType.ARROW -> {
                    (hitEntity as? Player)?.damage(BIG_NUMBER.toDouble(), hitEntity)
                    entity.remove()
                }
                EntityType.SNOWBALL -> {
                    // death snowball
                    (hitEntity as? Player)?.damage(BIG_NUMBER.toDouble(), hitEntity)
                    entity.world.strikeLightningEffect(entity.location)
                }
            }
        }
        listen<PlayerInteractEvent> {
            if (action != Action.LEFT_CLICK_BLOCK) return@listen
            if (!player.inGame) return@listen

            // shoot skull
            player.launchProjectile(WitherSkull::class.java, player.eyeLocation.direction)
            // cancel so player doesnt break anything
            isCancelled = true
        }


        listen<EntityDamageEvent> {
            if (entity !is Player) return@listen
            if (!entity.inGame) return@listen

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

    private inline fun <reified T : Event> listen(crossinline callback: T.() -> Unit) =
        PLUGIN.listen(callback = callback)
}
