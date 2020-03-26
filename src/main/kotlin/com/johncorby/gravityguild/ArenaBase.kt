package com.johncorby.gravityguild

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.block.Biome
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.EventExecutor
import java.io.File
import java.util.*

/**
 * return [ArenaInstance] that [Entity] is in
 */
val Entity.arenaIn get() = arenas.flatMap { it.value.instances }.find { world == it.world }

val arenas = mutableMapOf<String, ArenaBase>()

/**
 * base arena that [ArenaInstance]s are created from
 * template for instances
 * cant be joined
 * tracks instances
 */
class ArenaBase(val name: String) {
    val instances = mutableListOf<ArenaInstance>()
    private val worldName = "gg arena $name base"
    private val world: World

    init {
        // init world
        val generator = object : ChunkGenerator() {
            override fun generateChunkData(
                world: World,
                random: Random,
                x: Int,
                z: Int,
                biome: BiomeGrid
            ): ChunkData {
                for (bx in 0..15)
                    for (by in 0..255)
                        for (bz in 0..15)
                            biome.setBiome(bx, by, bz, Biome.THE_VOID)
                return createChunkData(world)
            }
        }
        world = WorldCreator(worldName)
            .generator(generator)
            .generateStructures(false)
            .createWorld()!!

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
    private val worldName = "gg arena ${base.name} instance $id"
    lateinit var world: World
    private val players = mutableListOf<Player>()

    init {
        // register teleport events for players
        Bukkit.getPluginManager().registerEvent(
            PlayerTeleportEvent::class.java,
            this,
            EventPriority.NORMAL,
            EventExecutor { _, event ->
                event as PlayerTeleportEvent
                when {
                    event.from == event.to -> return@EventExecutor
                    event.to == world -> event.player.joinArena()
                    event.from == world -> event.player.leaveArena()
                }
            },
            PLUGIN
        )

        // copy/load base world
        world.worldFolder.copyRecursively(
            File(Bukkit.getWorldContainer(), worldName),
            true
        )
        WorldCreator(worldName).createWorld()

        base.instances.add(this)
    }

    fun close() {
        // unregister event
        HandlerList.unregisterAll(this)

        // remove world
        Bukkit.unloadWorld(world, false)
        world.worldFolder.deleteRecursively()

        base.instances.remove(this)
    }

    private fun Player.joinArena() {
        TODO()
    }

    private fun Player.leaveArena() {
        TODO()
    }

    override fun equals(other: Any?) = (other as? ArenaInstance)?.id == id
    override fun hashCode() = id

    override fun toString() = worldName
}
