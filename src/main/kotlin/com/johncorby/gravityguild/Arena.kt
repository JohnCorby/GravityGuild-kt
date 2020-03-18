package com.johncorby.gravityguild

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

/**
 * return [Arena] that [Entity] is in
 */
val Entity.arenaIn: Arena?
    get() {
        for (arena in arenas.values)
            if (arena.bounds.`in`(location))
                return arena
        return null
    }

/**
 * defines area that [Arena] is in
 */
data class Bounds(val x1: Int, val z1: Int, val x2: Int, val z2: Int) {
    fun `in`(l: Location): Boolean = l.world.name == ARENA_WORLD && l.blockX in x1..x2 && l.blockZ in z1..z2
}

private const val ARENA_WORLD = "gg_arenas"
internal val arenas = mutableMapOf<String, Arena>()

class Arena(val name: String, var bounds: Bounds) {
    val entities = mutableListOf<Entity>()
    val players = mutableListOf<Player>()

    init {
        arenas[name] = this
    }

    fun remove() {
        arenas.remove(name)
    }

    fun close() {
        entities.forEach { it.remove() }
        entities.clear()

        loadSchematic()
    }

    fun Player.joinArena() {
        TODO()
    }

    fun Player.leaveArena() {
        TODO()
    }

    /**
     * load schematic to arena
     */
    private fun loadSchematic() {
        TODO()
//        TaskManager.IMP.async {
//            val file = File(PLUGIN.dataFolder, "$name.schematic")
//        }
    }

    /**
     * save arena to schematic
     */
    private fun saveSchematic() {
        TODO()
//        val region = CuboidRegion(
//            Vector(bounds.x1, 0, bounds.z1),
//            Vector(bounds.x2, 255, bounds.z2)
//        )
//        val copyWorld = EditSessionBuilder("world").autoQueue(false).build() // See https://github.com/boy0001/FastAsyncWorldedit/wiki/WorldEdit-EditSession
//
//        val pasteWorld = EditSessionBuilder("neworld").build() // See https://github.com/boy0001/FastAsyncWorldedit/wiki/WorldEdit-EditSession
//
//        val pos1 = Vector(10, 3, 10)
//        val pos2 = Vector(50, 90, 50)
//        val copyRegion = CuboidRegion(pos1, pos2)
//
//        val lazyCopy = copyWorld.lazyCopy(copyRegion)
//
//        val schem = Schematic(lazyCopy)
//        val pasteAir = true
//        val to = Vector(30, 10, 30)
//        schem.paste(pasteWorld, to, pasteAir)
//        pasteWorld.flushQueue()

//        TaskManager.IMP.async {
//            val region = CuboidRegion(
//                    Vector(bounds.x1, 0, bounds.z1),
//                    Vector(bounds.x2, 255, bounds.z2)
//            )
//            val clipboard = BlockArrayClipboard()
//            val schem = Schematic(region)
//        }
    }
}
