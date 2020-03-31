package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.Command
import com.johncorby.gravityguild.Data
import com.johncorby.gravityguild.PLUGIN
import com.johncorby.gravityguild.time
import hazae41.minecraft.kutils.bukkit.ConfigSection
import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.generator.ChunkGenerator
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.util.*

/**
 * return [ArenaInstance] that [Entity] is in
 */
val Entity.arenaIn get() = instances.find { world == it.world }

val arenas = mutableMapOf<String, ArenaBase>()
inline val instances get() = arenas.values.flatMap { it.instances }

/**
 * base arena that [ArenaInstance]s are created from
 * template for instances
 * cant be joined
 * tracks instances
 */
class ArenaBase(val name: String) : ConfigSection(Data, "arenas.$name") {
    private val worldName = "gg_arena_${name}_base"
    lateinit var world: World

    val instances = mutableListOf<ArenaInstance>()

    init {
        // init world
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
            world = WorldCreator(worldName)
                .generator(generator)
                .generateStructures(false)
                .createWorld()!!
            world.keepSpawnInMemory = false
        }

        arenas[name] = this

        parent[path] = "stub"
    }

    fun close() {
        // remove instances
        instances.forEach { it.close() }

        // delete world
        time("world $worldName removal") {
            // todo save option seems to do nothing, it takes just as long either way, see https://www.spigotmc.org/threads/loading-worlds-async-with-no-lag.268731/
            Bukkit.unloadWorld(world, false)
            world.worldFolder.deleteRecursively()
        }

        arenas.remove(name)

        parent[path] = null
    }

    override fun equals(other: Any?) = name == (other as? ArenaBase)?.name
    override fun hashCode() = name.hashCode()
}

/**
 * instance of [ArenaBase] where the actual games are held
 */
class ArenaInstance(val base: ArenaBase, val id: Int) : Listener {
    private val worldName = "gg_arena_${base.name}_instance_$id"
    lateinit var world: World

    private val players by lazy { server.onlinePlayers.filter { it.world == world } }

    private val coolDownTasks = mutableMapOf<Player, BukkitTask>()

    init {
        // copy/load base world
        time("world $worldName creation") {
            base.world.worldFolder.copyRecursively(
                File(Bukkit.getWorldContainer(), worldName),
                true
            )
            world = WorldCreator(worldName).createWorld()!!
            world.keepSpawnInMemory = false
            world.isAutoSave = false
        }

        base.instances.add(this)
    }

    fun close() {
        // teleport back to lobby
        players.forEach { Command.lobby(it) }

        // remove world
        time("world $worldName removal") {
            server.unloadWorld(world, false)
            world.worldFolder.deleteRecursively()
        }

        base.instances.remove(this)
    }

    fun onJoin(player: Player) = player.apply {
        // start cooldown
        // todo cooldown can be started any time and ended either after a certain period of time or on command (including during the certain period of time)


        coolDownTasks[player] = object : BukkitRunnable() {
            override fun run() {
                TODO()
            }
        }.runTaskLater(PLUGIN, 10 * 20)
    }

    fun onLeave(player: Player) = player.apply {

    }

    override fun equals(other: Any?) = id == (other as? ArenaInstance)?.id
    override fun hashCode() = id
}
