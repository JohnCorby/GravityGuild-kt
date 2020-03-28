package com.johncorby.gravityguild

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.generator.ChunkGenerator
import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

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
class ArenaBase(val name: String) {
    val instances = mutableListOf<ArenaInstance>()
    private val worldName = "gg_arena_${name}_base"
    lateinit var world: World

    init {
        // init world
        val generator = object : ChunkGenerator() {
            override fun generateChunkData(
                world: World,
                random: Random,
                x: Int,
                z: Int,
                biome: BiomeGrid
            ): ChunkData = createChunkData(world)
        }
        measureTimeMillis {
            world = WorldCreator(worldName)
                .generator(generator)
                .generateStructures(false)
                .createWorld()!!
        }.also {
            CONSOLE.info("world creation took ${it / 1000f} seconds")
        }
        world.keepSpawnInMemory = false

        arenas[name] = this
    }

    fun close() {
        // remove instances
        instances.forEach { it.close() }

        // delete world
        Bukkit.unloadWorld(world, false)
        world.worldFolder.deleteRecursively()

        arenas.remove(name)
    }

    override fun equals(other: Any?) = name == (other as? ArenaBase)?.name
    override fun hashCode() = name.hashCode()

    override fun toString() = worldName
}

/**
 * instance of [ArenaBase] where the actual games are held
 */
class ArenaInstance(val base: ArenaBase, val id: Int) : Listener {
    private val worldName = "gg_arena_${base.name}_instance_$id"
    lateinit var world: World
    private val players = mutableListOf<Player>()

    init {
        // copy/load base world
        base.world.worldFolder.copyRecursively(
            File(Bukkit.getWorldContainer(), worldName),
            true
        )
        measureTimeMillis {
            world = WorldCreator(worldName).createWorld()!!
        }.also {
            CONSOLE.info("world creation took ${it / 1000f} seconds")
        }
        world.isAutoSave = false
        world.keepSpawnInMemory = false

        base.instances.add(this)
    }

    fun close() {
        // todo teleport back to lobby
//        players.forEach { }

        // remove world
        Bukkit.unloadWorld(world, false)
        world.worldFolder.deleteRecursively()

        base.instances.remove(this)
    }

    fun onJoin(player: Player) {
        TODO()
    }

    fun onLeave(player: Player) {
        TODO()
    }

    override fun equals(other: Any?) = id == (other as? ArenaInstance)?.id
    override fun hashCode() = id

    override fun toString() = worldName
}
