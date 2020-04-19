package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.CONSOLE
import com.johncorby.gravityguild.debug
import com.johncorby.gravityguild.schedule
import org.bukkit.entity.Arrow
import org.bukkit.util.Vector

/**
 * a sorta hacky solution to prevent anti-gravity arrows from slowing to a stop
 */
object ArrowTracker {
    private val tracked = mutableMapOf<Arrow, Vector>()

    fun Arrow.startTracking() = tracked.put(this, velocity).also { CONSOLE.debug("start tracking $this") }
    fun Arrow.stopTracking() = tracked.remove(this).also { CONSOLE.debug("stop tracking $this") }
    fun stopTrackers() = tracked.clear()

    init {
        schedule(period = 20) {
            for ((arrow, velocity) in tracked)
                arrow.velocity = velocity
        }
    }
}
