package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.debug
import com.johncorby.gravityguild.schedule
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
        debug("start cooldown for $this")
    }

    fun Player.stopCooldown() {
        if (this !in tracked) return
        isInvincible = false
        tracked[this]!!.cancel()
        tracked.remove(this)
        debug("stop cooldown for $this")
    }
}
