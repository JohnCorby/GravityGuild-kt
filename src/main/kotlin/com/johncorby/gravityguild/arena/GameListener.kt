package com.johncorby.gravityguild.arena

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import com.johncorby.coreapi.*
import com.johncorby.gravityguild.arena.ArrowTracker.startTracking
import com.johncorby.gravityguild.arena.ArrowTracker.stopTracking
import com.johncorby.gravityguild.arena.CooldownTracker.startCooldown
import com.johncorby.gravityguild.arena.CooldownTracker.stopCooldown
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.entity.WitherSkull
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.*
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * handles the listening of game-related events.
 * this includes join/leave stuff.
 */
object GameListener : Listener {
    init {
        listen<PlayerJoinEvent> {
            player.warn("this plugin is actively in development!")
            player.warn("submit any bugs you find at https://github.com/johncorby/gravityguild-kt/issues")

            player.gameIn?.onJoin(player)
        }
        listen<PlayerQuitEvent> { player.gameIn?.onLeave(player) }

        listen<PlayerChangedWorldEvent> {
            val to = player.world
            games.find { it.world == to }?.onJoin(player)
            games.find { it.world == from }?.onLeave(player)
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
            // fixme Action.LEFT_CLICK_AIR fires when dropping items, equipping armor, and apparently sometimes placing blocks???
            //  maybe get a pull request to try and fix that
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

                game.broadcast(deathMessage!!)

                entity.lives--
                if (entity.lives > 0) {
                    game.broadcast("${entity.name} has ${unitize(entity.lives, "life", "lives")} remaining")
                    entity.initAndSpawn()
                    entity.stopCooldown()
                    entity.startCooldown()
                } else {
                    // death
                    game.broadcast("${entity.name} has ran out of lives!")
                    entity.isSpectating = true
                    entity.info("you are now spectating. leave at any time with /gg arena leave or /gg lobby")
                }

                if (game.numAlivePlayers <= 1){
                    // todo dont just instantly close it lol
                    game.close()
                }
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
