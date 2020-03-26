package com.johncorby.gravityguild

import hazae41.minecraft.kutils.bukkit.schedule

/**
 * run [block] that you can pause for one tick with [suspend]
 *
 * @param batch how many operations (stuff between suspends) to do each run
 */
fun runSuspendable(batch: Int = 1, block: suspend Suspendable.() -> Unit) {
    val iterator = sequence(block).iterator()

    PLUGIN.schedule(period = 1) {
        repeat(batch) {
            if (!iterator.hasNext()) {
                cancel()
                return@schedule
            }
            iterator.next()
        }
    }
}

private typealias Suspendable = SequenceScope<Any?>

suspend fun Suspendable.suspend() = yield(null)
