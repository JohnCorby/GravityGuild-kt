package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.info
import com.johncorby.coreapi.schedule
import com.johncorby.coreapi.unitize
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

/**
 * tracks cooldown for players
 */
object CooldownTracker {
    private val tracked = mutableMapOf<Player, BukkitTask>()
    private const val DELAY = 5

    fun Player.startCooldown() {
        if (this in tracked) return
        isInvincible = true
        tracked[this] = schedule(delay = DELAY * 20L) { stopCooldown() }
        info("you are invincible and glowing for ${unitize(DELAY, "second")}")
    }

    fun Player.stopCooldown() {
        if (this !in tracked) return
        isInvincible = false
        tracked[this]!!.cancel()
        tracked.remove(this)
        info("you are no longer invincible or glowing")
    }
}
