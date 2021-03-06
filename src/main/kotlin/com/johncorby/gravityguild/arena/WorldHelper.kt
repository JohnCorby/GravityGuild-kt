package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.commandError
import com.johncorby.coreapi.commandRequire
import com.johncorby.coreapi.time
import com.johncorby.coreapi.warn
import com.johncorby.gravityguild.Command.lobby
import hazae41.minecraft.kutils.bukkit.server
import hazae41.minecraft.kutils.get
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import java.util.*

/**
 * helper object for working with worlds
 */
object WorldHelper {
    private val creator = WorldCreator("")
        .generateStructures(false)
        .generator(object : ChunkGenerator() {
            override fun generateChunkData(
                world: World,
                random: Random,
                x: Int,
                z: Int,
                biome: BiomeGrid
            ) = createChunkData(world)
        })

    operator fun get(name: String) = server.getWorld(name) ?: commandError("world $name doesnt exist")

    /**
     * creates a world [name], or loads it if it already exists
     */
    fun createOrLoad(name: String): World {
        lateinit var world: World
        time("world $name create/load") {
            commandRequire(name.matches("""[a-z0-9/._-]+""".toRegex()), "world name $name has invalid character")
            world = WorldCreator(name).copy(creator).createWorld()
                ?: error("creation of world $name failed")
        }
        return world
    }

    /**
     * deletes a world [name]
     */
    fun delete(name: String) {
        time("world $name delete") {
            get(name).apply {
                players.forEach { it.lobby() }

                server.unloadWorld(this, false)
                worldFolder.deleteRecursively()
            }
        }
    }

    /**
     * copies world [from] to world [to] and loads that copy
     */
    fun copy(from: String, to: String): World {
        lateinit var world: World
        time("world copy from $from to $to") {
            val fromWorld = get(from)
            fromWorld.save()

            val fromWorldFolder = fromWorld.worldFolder
            val toWorldFolder = server.worldContainer[to]

            try {
                fromWorldFolder.copyRecursively(toWorldFolder)
                // deleting this ensures the server doesnt prevent loading this duplicated world
                toWorldFolder["uid.dat"].delete()
            } catch (e: FileAlreadyExistsException) {
                warn("world $to already exists. loading that one instead of copying from $from")
            }

            world = createOrLoad(to)
        }
        return world
    }
}
