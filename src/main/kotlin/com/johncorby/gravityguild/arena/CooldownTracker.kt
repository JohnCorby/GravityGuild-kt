package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.info
import com.johncorby.coreapi.schedule
import com.johncorby.coreapi.unitize
import com.johncorby.gravityguild.Config
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

/**
 * tracks cooldown for players
 */
object CooldownTracker {
    private val tracked = mutableMapOf<Player, BukkitTask>()
    private inline val DELAY get() = Config.COOLDOWN_TIME

    fun Player.startCooldown() {
        if (this in tracked) return
        isCooldown = true
        tracked[this] = schedule(delay = DELAY * 20L) { stopCooldown() }
        info("you are invincible and glowing for ${unitize(DELAY, "second")}")
    }

    fun Player.stopCooldown() {
        if (this !in tracked) return
        isCooldown = false
        tracked[this]!!.cancel()
        tracked.remove(this)
        info("you are no longer invincible or glowing")
    }
}
