package com.johncorby.gravityguild.arena

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import com.johncorby.coreapi.*
import com.johncorby.gravityguild.Config
import com.johncorby.gravityguild.arena.CooldownTracker.startCooldown
import com.johncorby.gravityguild.arena.CooldownTracker.stopCooldown
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldInitEvent

/**
 * listens to events related to general arenas
 */
object ArenaListener : Listener {
    init {
        listen<WorldInitEvent> {
            world.isAutoSave = false
            world.keepSpawnInMemory = false
        }



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






        listen<PlayerDeathEvent> {
            entity.gameIn?.let { game ->
                isCancelled = true

                keepInventory = true
                keepLevel = true

                game.broadcast(deathMessage!!)

                entity.lives--
                if (entity.lives > 0) {
                    // respawn
                    game.broadcast("${entity.name} has ${unitize(entity.lives, "life", "lives")} remaining")
                    entity.initAndSpawn()
                    entity.stopCooldown()
                    entity.startCooldown()
                } else {
                    // death
                    game.broadcast("${entity.name} has ran out of lives!")
                    entity.isSpectating = true
                    entity.info("you are now spectating. leave at any time with /gg arena leave or /gg lobby")

                    // win state
                    if (game.numAlivePlayers <= 1) {
                        // todo maybe refactor this to start handler?
                        val winnerName = game.world.players.find { !it.isSpectating }?.name ?: "nobody"
                        game.broadcast("$winnerName has won! good job.")
                        schedule(Config.WIN_WAIT_TIME * 20L) { game.close() }
                    }
                }
            }
        }



        listen<PlayerPickupExperienceEvent> {
            if (player.inGame) isCancelled = true
        }
    }
}
