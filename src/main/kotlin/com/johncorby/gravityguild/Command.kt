package com.johncorby.gravityguild

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.PrintWriter
import java.io.StringWriter

private const val ADMIN_PERM = "gravityguild.admin"

@CommandAlias("gravityguild|gg")
object Command : BaseCommand() {
    init {
        PaperCommandManager(PLUGIN).apply {
            registerCommand(this@Command)

            enableUnstableAPI("help")

            // arena tab completion
            commandCompletions.registerCompletion("arena") { c -> arenas.keys.filter { it.startsWith(c.input) } }

            // error handler
            setDefaultExceptionHandler { _, _, sender, _, t ->
                sender.getIssuer<CommandSender>().error("we made a fucky wucky!!! (check console for exception :3)")
                val writer = StringWriter()
                val stream = PrintWriter(writer)
                t.printStackTrace(stream)
                Bukkit.getConsoleSender().error(writer.toString())
                true
            }
        }
    }

    @HelpCommand
    fun help(player: Player, help: CommandHelp) {
        // todo maybe refactor to a messaging system?
        player.info("--- help ---")
        help.showHelp()
    }

    @Subcommand("reload|r")
    @CommandPermission(ADMIN_PERM)
    @Description("reloads plugin (for debugging)")
    fun reload(player: Player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman reload ${PLUGIN.name}")
        player.info("it is done")
    }

    @Subcommand("arena add|a")
    @CommandPermission(ADMIN_PERM)
    @Description("adds an arena by name")
    fun addArena(player: Player, name: String) {
        TODO()
    }

    @Subcommand("arena delete|d")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arena")
    @Description("removes an arena by name")
    fun delArena(player: Player, name: String) {
        TODO()
    }
}