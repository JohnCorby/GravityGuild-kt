/**
 * terminology:
 * arena name: the thing the player inputs in commands
 * map world: the maps that creators can edit
 * game/game world: the object/world that people can play on
 */
package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.commandError
import com.johncorby.coreapi.info
import com.johncorby.gravityguild.Config
import com.johncorby.gravityguild.arena.CooldownTracker.stopCooldown
import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

/**
 * checks if [Entity] is in a game world
 */
val Entity.inGame get() = world.name.endsWith(GAME_WORLD_SUFFIX)

/**
 * return [ArenaGame] that [Entity] is in
 */
val Entity.gameIn
    get() = if (!inGame) null
    else games.find { it.world == world }
        ?: error("entity $this is in game world ${world.name} with no associated ArenaGame")

const val MAP_WORLD_SUFFIX = "_gg_map"
const val GAME_WORLD_SUFFIX = "_gg_game"
val maps: Map<String, World>
    get() = server.worlds
        .filter { it.name.endsWith(MAP_WORLD_SUFFIX) }
        .associateBy { it.name.dropLast(MAP_WORLD_SUFFIX.length) }

val games = mutableListOf<ArenaGame>()


typealias ArenaMap = Pair<String, World>

inline val ArenaMap.name get() = first
inline val ArenaMap.world get() = second



fun ArenaGame.broadcast(message: String) = world.players.forEach { info(message) }

/**
 * instance of [ArenaWorld] where the actual games are held
 */
class ArenaGame(val name: String = maps.keys.random()) {
    private val startHandler = StartHandler(this)
    val isJoinable = !startHandler.hasStarted && numAlivePlayers < Config.MAX_PLAYERS

    private fun generateId(): Int = games.map { it.id }.let { ids ->
        var newId = 0
        while (newId in ids) newId++
        newId
    }

    val id = generateId()

    private val worldName = "$name$id$GAME_WORLD_SUFFIX"
    val world: World

    // fixme java npe?!?!?!???!?!?!?!
    val numAlivePlayers get() = world.players.filter { !it.isSpectating }.size

    init {
        WorldHelper.copy((maps[name] ?: commandError("arena $name doesnt exist")).name, worldName)
        world = WorldHelper.getWorld(worldName)

        games.add(this)
    }

    fun close() {
        // stop tracking arrows
        ArrowTracker.stopTracking()
        // stop countdown
        startHandler.stopCountdown()

        WorldHelper.delete(worldName)

        games.remove(this)
    }

    /**
     * called after a join occurs
     */
    fun onJoin(player: Player) = player.apply {
        isSpectating = false
        lives = Config.LIVES
        isInvincible = true
        // todo put players somewhere else instead of just leaving them in the map before the game starts
    }

    /**
     * called after a leave occurs
     */
    fun onLeave(player: Player) = player.apply {
        stopCooldown()

        // close game if only one player left
        // fixme probably wont call close twice if onLeave was called when closing game and kicking players out
        if (numAlivePlayers <= 1) close()
    }

    override fun equals(other: Any?) = (other as? ArenaGame)?.let {
        it.name == name && it.id == id
    } ?: false

    override fun hashCode() = Objects.hash(name, id)
}
