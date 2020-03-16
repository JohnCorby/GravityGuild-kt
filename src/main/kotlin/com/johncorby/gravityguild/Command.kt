package com.johncorby.gravityguild

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

private const val ADMIN_PERM = "gravityguild.admin"
private val MSG_PREFIX = "${ChatColor.AQUA}[GravityGuild]${ChatColor.RESET}"

@CommandAlias("gravityguild|gg")
object Command : BaseCommand() {
    init {
        PaperCommandManager(PLUGIN).apply {
            registerCommand(this@Command)

            enableUnstableAPI("help")

            setDefaultExceptionHandler { command, registeredCommand, sender, args, t ->
                sender.sendMessage("$MSG_PREFIX${ChatColor.RED} we made a fucky wucky!!! (check console for exception :3)")
                PLUGIN.logger.severe(t.toString())
                true
            }
        }
    }

    @HelpCommand
    fun help(sender: CommandSender, help: CommandHelp) {
        sender.sendMessage("--- $MSG_PREFIX help ---")
        help.showHelp()
    }

    @Subcommand("reload|r")
    @CommandPermission(ADMIN_PERM)
    @Description("reloads plugin (for debugging)")
    fun reload(player: Player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman reload ${PLUGIN.name}")
        player.sendMessage("$MSG_PREFIX it is done")
    }

    @Subcommand("arena add|a")
    @CommandPermission(ADMIN_PERM)
    @Description("adds an arena by name")
    @Syntax("<name>")
    fun addArena(player: Player, name: String) {
        TODO()
    }

    @Subcommand("arena delete|d")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arena") //todo
    @Description("removes an arena by name")
    @Syntax("<name>")
    fun delArena(player: Player, name: String) {
        TODO()
    }
}
