/**
 * functions for sending different types of messages to players, consoles, and arenas
 *
 * info is a generic message
 * warn is when something goes wrong but can still be handled
 * error is when something goes wrong and cant be handled (exception)
 * debug is for info that can be turned off in config
 */
package com.johncorby.gravityguild

import com.johncorby.gravityguild.arena.ArenaGame
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

private fun CommandSender.send(color: ChatColor, message: String) =
    sendMessage("${ChatColor.AQUA}[GravityGuild] $color$message")

fun CommandSender.info(message: String) = send(ChatColor.RESET, message)
fun CommandSender.warn(message: String) = send(ChatColor.YELLOW, message)
fun CommandSender.error(message: String) = send(ChatColor.RED, message)
fun CommandSender.debug(message: String) {
    if (Options.debug) send(ChatColor.GREEN, message)
}

inline val CONSOLE get() = PLUGIN.server.consoleSender
fun info(message: String) = CONSOLE.info(message)
fun warn(message: String) = CONSOLE.warn(message)
fun debug(message: String) = CONSOLE.debug(message)

fun ArenaGame.broadcast(message: String) = world.players.forEach { it.send(ChatColor.YELLOW, message) }
