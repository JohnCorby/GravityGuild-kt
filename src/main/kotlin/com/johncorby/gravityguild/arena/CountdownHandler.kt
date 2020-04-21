package com.johncorby.gravityguild.arena

import com.johncorby.gravityguild.Config
import com.johncorby.gravityguild.broadcast
import com.johncorby.gravityguild.schedule
import com.johncorby.gravityguild.unitize

/**
 * handles counting down the arena
 * maybe refactor this into arena later
 */
class CountdownHandler(private val game: ArenaGame) {
    private companion object {
        private const val DURATION = 20
        private val PRINT_WHEN = intArrayOf(DURATION, 10, 5, 4, 3, 2, 1)
    }

    /**
     * if the arena has started or if we are still waiting for more players
     */
    var isRunning = false
        private set

    /**
     * start the game
     */
    private fun startGame() {
        isRunning = true
        game.broadcast("let the games begin!")

        game.world.players.forEach { it.respawn() }
    }

    private var countdown = DURATION
    private var task = schedule(period = 20) {
        if (countdown in PRINT_WHEN)
            game.broadcast("game starting in ${unitize(countdown, "second")}")

        if (countdown <= 0) {
            if (game.numPlayers < Config.minPlayers) {
                game.broadcast("game needs at least ${unitize(Config.minPlayers, "player")} to start")
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
