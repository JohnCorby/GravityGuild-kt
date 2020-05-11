package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.schedule
import com.johncorby.coreapi.unitize
import com.johncorby.gravityguild.Config
import com.johncorby.gravityguild.arena.CooldownTracker.startCooldown

/**
 * handles starting the [ArenaGame]
 * maybe refactor this into arena later
 */
class StartHandler(private val game: ArenaGame) {
    /**
     * if the game has started or if we are still waiting for more players
     */
    var hasStarted = false
        private set

    /**
     * start the game
     */
    private fun startGame() {
        hasStarted = true
        game.broadcast("let the games begin!")

        game.world.players.forEach {
            it.startCooldown()
        }
    }


    private inline val DURATION get() = Config.COUNTDOWN_INTERVALS.max()!!

    private var countdown = DURATION
    private var task = schedule(period = 20) {// fixme doesnt show first number
        if (countdown in Config.COUNTDOWN_INTERVALS)
            game.broadcast("game starting in ${unitize(countdown, "second")}")

        if (countdown <= 0) {
            if (game.numAlivePlayers < Config.MIN_PLAYERS) {
                game.broadcast("game needs at least ${unitize(Config.MIN_PLAYERS, "player")} to start")
                countdown = DURATION
            } else {
                startGame()
                stopCountdown()
                return@schedule
            }
        }

        countdown--
    }

    /**
     * stop the countdown (possibly prematurely)
     */
    fun stopCountdown(): Unit = task.cancel()
}
