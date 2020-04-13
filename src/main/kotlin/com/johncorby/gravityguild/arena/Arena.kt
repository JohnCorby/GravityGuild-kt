package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.Command
import com.johncorby.gravityguild.time
import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.generator.ChunkGenerator
import java.io.File
import java.util.*

/**
 * return [ArenaGame] that [Entity] is in
 */
val Entity.arenaIn get() = arenaGames.find { world == it.world }
inline val Entity.inArena get() = arenaIn != null

val arenaWorlds = mutableMapOf<String, World>()
val arenaGames = mutableListOf<ArenaGame>()

/**
 * a base [World] to be copied by each [ArenaGame]
 */
object ArenaWorld {
    /**
     * create arena world from [name]
     */
    fun create(name: String) {
        val worldName = "gg_arena_${name}_base"
        time("world $worldName creation") {
            val generator = object : ChunkGenerator() {
                override fun generateChunkData(
                    world: World,
                    random: Random,
                    x: Int,
                    z: Int,
                    biome: BiomeGrid
                ): ChunkData = createChunkData(world)
            }
            val world = WorldCreator(worldName)
                .generator(generator)
                .generateStructures(false)
                .createWorld()!!
            world.keepSpawnInMemory = false
        }
    }

    /**
     * delete arena [world]
     */
    fun delete(world: World) {
        time("world ${world.name} removal") {
            // todo save option seems to do nothing, it takes just as long either way, see https://www.spigotmc.org/threads/loading-worlds-async-with-no-lag.268731/
            Bukkit.unloadWorld(world, false)
            world.worldFolder.deleteRecursively()
        }
    }
}

/**
 * instance of [ArenaWorld] where the actual games are held
 */
class ArenaGame(val name: String, val id: Int) : Listener {
    private val worldName = "gg_arena_${name}_instance_$id"
    lateinit var world: World

    private val players get() = server.onlinePlayers.filter { it.world == world }

    init {
        // copy/load base world
        time("world $worldName creation") {
            arenaWorlds[name]!!.worldFolder.copyRecursively(
                File(Bukkit.getWorldContainer(), worldName),
                true
            )
            world = WorldCreator(worldName).createWorld()!!
            world.keepSpawnInMemory = false
            world.isAutoSave = false
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

        // close game is no more players
        if (players.isEmpty()) close()
    }

    override fun equals(other: Any?) = (other as? ArenaGame)?.let {
        it.name == name && it.id == id
    } ?: false

    override fun hashCode() = Objects.hash(name, id)
}
