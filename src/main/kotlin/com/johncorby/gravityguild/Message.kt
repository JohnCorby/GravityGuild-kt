package com.johncorby.gravityguild

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

val CONSOLE get() = PLUGIN.server.consoleSender
fun info(message: String) = CONSOLE.info(message)
fun warn(message: String) = CONSOLE.warn(message)
fun error(message: String) = CONSOLE.error(message)
fun debug(message: String) = CONSOLE.debug(message)
