package com.johncorby.gravityguild

import hazae41.minecraft.kutils.bukkit.schedule

/**
 * uses [sequence] and bukkit scheduling to run a suspendable function
 *
 * @param batch how many operations (stuff between suspends) to do each run
 */
fun runSuspendable(batch: Int = 1, block: suspend Suspendable.() -> Unit) {
    val sequence = sequence<Int> {
        block()
        yield(SUSPEND_END)
    }

    PLUGIN.schedule(period = 1) {
        val result = sequence.take(batch)
        if (SUSPEND_END in result) cancel()
    }
}

private typealias Suspendable = SequenceScope<Int>

private const val SUSPEND_PAUSE = 0
private const val SUSPEND_END = 1

suspend fun Suspendable.suspend() = yield(SUSPEND_PAUSE)
