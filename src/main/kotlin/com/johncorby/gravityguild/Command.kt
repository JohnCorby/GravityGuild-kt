package com.johncorby.gravityguild

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
                sender.getIssuer<CommandSender>().apply {
                    error("we made a fucky wucky!!! (check console for exception :3)")
                    error("error is: $t")
                }
                true
            }
        }
    }

    @HelpCommand
    fun help(sender: CommandSender, help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("reload")
    @CommandPermission(ADMIN_PERM)
    @Description("reloads plugin (for debugging)")
    fun reload(sender: CommandSender) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman reload ${PLUGIN.name}")
        sender.info("it is done")
    }

    @Subcommand("arena add")
    @CommandPermission(ADMIN_PERM)
    @Description("adds an arena by name")
    fun addArena(sender: Player, name: String) {
        if (name in arenas) throw InvalidCommandArgument("arena $name already exists")
        ArenaBase(name).apply {
            sender.teleport(Location(
                world,
                sender.location.x,
                sender.location.y,
                sender.location.z
            ))
        }

        sender.info("arena $name created")
    }

    @Subcommand("arena delete")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arena")
    @Description("removes an arena by name")
    fun delArena(sender: CommandSender, name: String) {
        if (name !in arenas) throw InvalidCommandArgument("arena $name doesnt exist")
        arenas[name]!!.close()
        sender.info("arena $name deleted")
    }

    @Subcommand("setlobby")
    @CommandPermission(ADMIN_PERM)
    @Description("set location for lobby")
    fun setLobby(sender: Player) {
        // todo set lobby
        sender.info("lobby set to current location")
    }
}
