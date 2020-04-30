package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.listen
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

/**
 * handles
 * placing/breaking of signs,
 * updating text.
 */
object SignHandler : Listener {
    init {
//        // note: physics event gets called when a block changes
//        // see https://bukkit.org/threads/at-what-occasions-the-blockphysicsevent-is-triggered.181249/
//        // and https://github.com/PaperMC/Paper/issues/1867
//        listen<BlockPhysicsEvent> {
//            // if it was previously a sign and now its not, dont do shit
//            if (this.block)
//        }

        listen<BlockPlaceEvent> {

        }
    }

    // todo
}
