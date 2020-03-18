package com.johncorby.gravityguild

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

private fun CommandSender.send(color: ChatColor, message: String) =
    sendMessage("${ChatColor.AQUA}[GravityGuild]$color$message")

fun CommandSender.info(message: String) = send(ChatColor.RESET, message)
fun CommandSender.debug(message: String) = send(ChatColor.GREEN, message)
fun CommandSender.warn(message: String) = send(ChatColor.YELLOW, message)
fun CommandSender.error(message: String) = send(ChatColor.RED, message)