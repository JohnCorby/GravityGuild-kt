package com.johncorby.gravityguild.arena

import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

/**
 * return [ArenaGame] that [Entity] is in
 */
val Entity.arenaIn get() = arenaGames.find { world == it.world }
inline val Entity.inArena get() = arenaIn != null

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
        WorldHelper.copy(arenaWorlds[name]!!.name, worldName)

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
    }

    /**
     * called after a leave occurs
     */
    fun onLeave(player: Player) = player.apply {
        // todo stop cooldowns

        // close game if only one player left
        // fixme probably wont call close twice if onLeave was called when closing game and kicking players out
        if (numPlayers <= 1) close()
    }

    override fun equals(other: Any?) = (other as? ArenaGame)?.let {
        it.name == name && it.id == id
    } ?: false

    override fun hashCode() = Objects.hash(name, id)
}
