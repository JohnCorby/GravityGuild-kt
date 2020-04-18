/**
 * terminology:
 * arena name: the thing the player puts in
 * base world: the maps that creators can edit
 * game/game world: the object/world that people can play on
 */
package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.Options
import com.johncorby.gravityguild.orNullError
import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
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
    else arenaGames.find { it.world == world }
        ?: error("entity $this is in game world ${world.name} with no associated ArenaGame")

const val BASE_WORLD_SUFFIX = "_gg_base"
const val GAME_WORLD_SUFFIX = "_gg_game"
val arenaWorlds
    get() = server.worlds
        .filter { it.name.endsWith(BASE_WORLD_SUFFIX) }
        .associateBy { it.name.dropLast(BASE_WORLD_SUFFIX.length) }

val arenaGames = mutableListOf<ArenaGame>()

/**
 * instance of [ArenaWorld] where the actual games are held
 */
class ArenaGame : Listener {
    val name = arenaWorlds.keys.random()

    private fun generateId(): Int = arenaGames.map { it.id }.let { ids ->
        var newId = 0
        while (newId in ids) newId++
        newId
    }

    val id = generateId()

    val worldName = "$name$id$GAME_WORLD_SUFFIX"
    inline val world get() = WorldHelper.getWorld(worldName)

    inline val numPlayers get() = world.playerCount

    init {
        WorldHelper.copy(arenaWorlds[name].orNullError("base world for arena $name").name, worldName)

        arenaGames.add(this)
    }

    fun close() {
        WorldHelper.delete(worldName)

        arenaGames.remove(this)
    }

    /**
     * called after a join occurs
     */
    fun onJoin(player: Player) = player.apply {
        // todo start cooldown
        //  cooldown can be started any time and ended either after a certain period of time or on command (including during the certain period of time)

        lives = Options.lives
        initForArena()
    }

    /**
     * called after a leave occurs
     */
    fun onLeave(player: Player) = player.apply {
        // todo stop cooldowns

        // close game if only one player left
        // fixme probably wont call close twice if onLeave was called when closing game and kicking players out
        if (numPlayers <= 1) close()

        isInvincible = false
    }

    override fun equals(other: Any?) = (other as? ArenaGame)?.let {
        it.name == name && it.id == id
    } ?: false

    override fun hashCode() = Objects.hash(name, id)
}
