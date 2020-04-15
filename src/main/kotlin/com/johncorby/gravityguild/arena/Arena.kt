package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.Command
import com.johncorby.gravityguild.time
import hazae41.minecraft.kutils.bukkit.server
import hazae41.minecraft.kutils.get
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.generator.ChunkGenerator
import java.util.*

/**
 * return [ArenaGame] that [Entity] is in
 */
val Entity.arenaIn get() = arenaGames.find { world == it.world }
inline val Entity.inArena get() = arenaIn != null

const val WORLD_PREFIX = "gg_arena_"
const val GAME_WORLD_SUFFIX = "_game"
val arenaWorlds
    get() = server.worlds
        .filter { it.name.startsWith(WORLD_PREFIX) && !it.name.endsWith(GAME_WORLD_SUFFIX) }
        .associateBy { it.name.drop(WORLD_PREFIX.length) }
val arenaGames = mutableListOf<ArenaGame>()

private val WORLD_CREATOR = WorldCreator("")
    .generateStructures(false)
    .generator(object : ChunkGenerator() {
        override fun generateChunkData(
            world: World,
            random: Random,
            x: Int,
            z: Int,
            biome: BiomeGrid
        ): ChunkData = createChunkData(world)
    })

/**
 * a base [World] to be copied by each [ArenaGame]
 */
object ArenaWorld {
    /**
     * create arena world from [name]
     */
    fun create(name: String) {
        // todo since copying and loading is faster than creating, only create 1 empty world and then copy from it for both base worlds AND game worlds
        val worldName = "$WORLD_PREFIX$name"
        time("world $worldName creation") {
            val world = WorldCreator(worldName).copy(WORLD_CREATOR).createWorld()!!
            world.keepSpawnInMemory = false
            // we'll save the world manually on game creation and it saves itself on server close
            world.isAutoSave = false
        }
    }

    /**
     * delete arena [world]
     */
    fun delete(world: World) {
        world.players.forEach { Command.lobby(it) }

        time("world ${world.name} removal") {
            Bukkit.unloadWorld(world, false)
            world.worldFolder.deleteRecursively()
        }
    }
}

/**
 * instance of [ArenaWorld] where the actual games are held
 */
class ArenaGame : Listener {
    private val name = arenaWorlds.keys.random()

    private fun generateId(): Int = arenaGames.map { it.id }.let { ids ->
        var newId = 0
        while (newId in ids) newId++
        newId
    }

    private val id = generateId()

    private val worldName = "$WORLD_PREFIX$name$id$GAME_WORLD_SUFFIX"
    lateinit var world: World

    private val players get() = server.onlinePlayers.filter { it.world == world }
    val numPlayers get() = players.size

    init {
        // copy/load base world
        time("world $worldName creation") {
            arenaWorlds[name]!!.let { baseWorld ->
                baseWorld.save()

                val baseWorldFolder = baseWorld.worldFolder
                val gameWorldFolder = server.worldContainer[worldName]
                // todo sometimes does this: java.io.IOException: Source file wasn't copied completely, length of destination file differs.
                baseWorldFolder.copyRecursively(gameWorldFolder, true)
                // deleting this ensures the server doesnt prevent loading this duplicated world
                gameWorldFolder["uid.dat"].delete()

                world = WorldCreator(worldName).copy(WORLD_CREATOR).createWorld()!!
                world.keepSpawnInMemory = false
                world.isAutoSave = false
            }
        }
        arenaGames.add(this)
    }

    fun close() {
        // teleport back to lobby
        players.forEach { Command.lobby(it) }

        // remove world
        time("world $worldName removal") {
            server.unloadWorld(world, false)
            world.worldFolder.deleteRecursively()
        }

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
        // todo probably wont call close twice if onLeave was called when closing game and kicking players out
        if (players.size <= 1) close()
    }

    override fun equals(other: Any?) = (other as? ArenaGame)?.let {
        it.name == name && it.id == id
    } ?: false

    override fun hashCode() = Objects.hash(name, id)
}
