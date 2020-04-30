package com.johncorby.gravityguild.arena

import com.johncorby.coreapi.schedule
import com.johncorby.coreapi.unitize
import com.johncorby.gravityguild.Config

/**
 * handles starting the [ArenaGame]
 * maybe refactor this into arena later
 */
class StartHandler(private val game: ArenaGame) {
    private companion object {
        private const val DURATION = 20
        private val PRINTED_VALUES = intArrayOf(DURATION, 10, 5, 4, 3, 2, 1)
    }

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

        game.world.players.forEach { it.respawn() }
    }

    private var countdown = DURATION
    private var task = schedule(period = 20) {
        if (countdown in PRINTED_VALUES)
            game.broadcast("game starting in ${unitize(countdown, "second")}")

        if (countdown <= 0) {
            if (game.numAlivePlayers < Config.MIN_PLAYERS) {
                game.broadcast("game needs at least ${unitize(Config.MIN_PLAYERS, "player")} to start")
                countdown = DURATION
            } else {
                startGame()
                return@schedule stopCountdown()
            }
        }

        countdown--
    }

    /**
     * stop the countdown (possibly prematurely)
     */
    fun stopCountdown(): Unit = task.cancel()
}
