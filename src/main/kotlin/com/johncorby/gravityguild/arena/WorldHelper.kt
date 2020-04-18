package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.Command
import com.johncorby.gravityguild.orNullError
import com.johncorby.gravityguild.time
import com.johncorby.gravityguild.warn
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
            ): ChunkData = createChunkData(world)
        })

    fun getWorld(name: String) = server.getWorld(name) ?: error("world $name doesnt exist")

    /**
     * creates a world [name], or loads it if it already exists
     */
    fun createOrLoad(name: String) {
        // todo since copying and loading is faster than creating, only create 1 empty world and then copy from it for both base worlds AND game worlds
        time("world $name create/load") {
            require(name.matches("""[a-z0-9/._-]+""".toRegex())) { "world name $name has invalid character" }

            WorldCreator(name).copy(creator).createWorld()
                .orNullError("createWorld for world $name")
                .apply {
                    keepSpawnInMemory = false
                    isAutoSave = false
                }
        }
    }

    /**
     * deletes a world [name]
     */
    fun delete(name: String) {
        time("world $name delete") {
            getWorld(name).apply {
                players.forEach { Command.lobby(it) }

                server.unloadWorld(this, false)
                worldFolder.deleteRecursively()
            }
        }
    }

    /**
     * copies world [from] to world [to] and loads that copy
     */
    fun copy(from: String, to: String) {
        time("world copy from $from to $to") {
            val fromWorld = getWorld(from)
            fromWorld.save()

            val fromWorldFolder = fromWorld.worldFolder
            val toWorldFolder = server.worldContainer[to]

            try {
                // fixme sometimes throws java.io.IOException: Source file wasn't copied completely, length of destination file differs.
                fromWorldFolder.copyRecursively(toWorldFolder)
                // deleting this ensures the server doesnt prevent loading this duplicated world
                toWorldFolder["uid.dat"].delete()
            } catch (e: FileAlreadyExistsException) {
                warn("world $to already exists. loading that one instead of copying from $from")
            }

            createOrLoad(to)
        }
    }
}
