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
    // todo check world
    fun `in`(l: Location): Boolean = l.blockX in x1..x2 && l.blockZ in z1..z2
}

private val arenas = mutableMapOf<String, Arena>()

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

        load()
    }

    fun load(): Nothing = TODO("fawe loading")
    fun save(): Nothing = TODO("fawe saving")
}
