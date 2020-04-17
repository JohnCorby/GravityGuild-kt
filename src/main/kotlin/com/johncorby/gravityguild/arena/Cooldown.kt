package com.johncorby.gravityguild.arena

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

/**
 * represents invincibility cooldown for players
 */
class Cooldown(val game: ArenaGame, val player: Player) : BukkitRunnable() {
    fun start() {
        // todo glow, invincible
    }

    override fun run() {
        TODO()
    }
}
