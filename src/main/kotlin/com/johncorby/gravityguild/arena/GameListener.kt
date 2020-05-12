package com.johncorby.gravityguild.arena

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent
import com.johncorby.coreapi.BIG_NUMBER
import com.johncorby.coreapi.listen
import com.johncorby.gravityguild.arena.ArrowTracker.startTracking
import com.johncorby.gravityguild.arena.ArrowTracker.stopTracking
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.entity.WitherSkull
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * listens to events related to the game
 */
object GameListener : Listener {
    init {
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
            if (action !in arrayOf(Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR)) return@listen

            // shoot skull
            player.launchProjectile(WitherSkull::class.java, player.eyeLocation.direction)
            // cancel so player doesnt break anything
            isCancelled = true
        }




        listen<EntityDamageEvent> {
            if (!entity.inGame) return@listen
            if (entity !is Player) return@listen

            // out of world damage is usually not accounted for by invulnerability/no health gamemode
            if (entity.isInvulnerable || (entity as Player).isSpectating) {
                isCancelled = true
                return@listen
            }

            if (cause !in arrayOf(DamageCause.FALL, DamageCause.ENTITY_EXPLOSION)) return@listen
            if ((entity as Player).health - damage <= 0) return@listen
            damage = 0.0
        }




        listen<FoodLevelChangeEvent> {
            if (entity.inGame) isCancelled = true
        }
    }
}
