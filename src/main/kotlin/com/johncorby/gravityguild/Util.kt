package com.johncorby.gravityguild

import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.scheduler.BukkitRunnable
import kotlin.system.measureTimeMillis

/**
 * run [block] that you can pause for one tick with [suspend]
 *
 * @param batch how many operations (stuff between suspends) to do each run
 */
fun runSuspendable(batch: Int = 1, block: suspend Suspendable.() -> Unit) {
    val iterator = iterator(block)

    object : BukkitRunnable() {
        override fun run() {
            repeat(batch) {
                if (!iterator.hasNext()) return cancel()
                iterator.next()
            }
        }
    }.runTaskTimer(PLUGIN, 0, 0)
}

private typealias Suspendable = SequenceScope<Any?>

suspend fun Suspendable.suspend() = yield(null)



/**
 * run [block] and print how long it took
 */
fun time(what: String, block: () -> Unit) =
    (measureTimeMillis(block) / 1000f).also { CONSOLE.debug("$what took $it seconds") }
