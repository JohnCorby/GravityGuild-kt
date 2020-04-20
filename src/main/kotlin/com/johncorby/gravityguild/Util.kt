package com.johncorby.gravityguild

import co.aikar.commands.InvalidCommandArgument
import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask
import kotlin.system.measureTimeMillis

/**
 * a nice big number that minecraft likes and that isnt big enough to somehow not work :)
 */
const val BIG_NUMBER = 9999


/**
 * create a new [Listener] object
 */
fun newListener() = object : Listener {}

/**
 * listen for an event with this [Listener]
 *
 * modified from the hazae41 one
 */
inline fun <reified T : Event> Listener.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline callback: T.() -> Unit
) = server.pluginManager.registerEvent(
    T::class.java,
    this,
    priority,
    { _, event -> if (event is T) event.callback() },
    PLUGIN,
    ignoreCancelled
)

/**
 * stop listening for all events that are listened to by this [Listener]
 */
fun Listener.stopListening() = HandlerList.unregisterAll(this)


/**
 * schedule a [BukkitTask]
 *
 * modified from the hazae41 one, which was stupid with time
 * todo check decompiled code to see if when is optimized away when inlining
 */
inline fun schedule(
    delay: Long = 0,
    period: Long = 0,
    async: Boolean = false,
    crossinline block: BukkitTask.() -> Unit
): BukkitTask {
    lateinit var task: BukkitTask
    val runnable = Runnable { task.block() }
    task = server.scheduler.run {
        when {
            period > 0 ->
                if (async) runTaskTimerAsynchronously(PLUGIN, runnable, delay, period)
                else runTaskTimer(PLUGIN, runnable, delay, period)
            delay > 0 ->
                if (async) runTaskLaterAsynchronously(PLUGIN, runnable, delay)
                else runTaskLater(PLUGIN, runnable, delay)
            else ->
                if (async) runTaskAsynchronously(PLUGIN, runnable)
                else runTask(PLUGIN, runnable)
        }
    }
    return task
}


/**
 * run [block] that you can pause for [period] tick/s with [suspend]
 *
 * @param batch how many operations (stuff between suspends) to do each run
 * @param period how many ticks to wait on each suspend, or how many ticks between each run of the "event loop"
 */
fun runSuspendable(batch: Int = 1, period: Long = 1, block: suspend Suspendable.() -> Unit) {
    val iterator = iterator(block)

    schedule(period = period) {
        repeat(batch) {
            if (iterator.hasNext()) iterator.next()
            else cancel()
        }
    }
}
private typealias Suspendable = SequenceScope<Any?>

suspend inline fun Suspendable.suspend() = yield(null)


/**
 * run [block] and print how long it took
 */
inline fun time(what: String, block: () -> Unit) = (measureTimeMillis(block) * 20 / 1000f)
    .also { debug("$what took $it ticks") }

/**
 * returns [value] and a unit that is [singular] or [plural] depending on [value]
 */
fun unitize(value: Number, singular: String, plural: String) = "$value ${if (value == 1) singular else plural}"


fun commandError(message: Any): Nothing = throw InvalidCommandArgument(message.toString())
fun commandRequire(value: Boolean, message: Any) {
    if (!value) commandError(message)
}
