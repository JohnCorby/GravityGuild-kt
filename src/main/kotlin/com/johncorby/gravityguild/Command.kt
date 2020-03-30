package com.johncorby.gravityguild

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import com.johncorby.gravityguild.arena.ArenaBase
import com.johncorby.gravityguild.arena.arenas
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

private const val ADMIN_PERM = "gravityguild.admin"

@CommandAlias("gravityguild|gg")
object Command : BaseCommand() {
    init {
        PaperCommandManager(PLUGIN).apply {
            enableUnstableAPI("help")

            // arena
            commandCompletions.registerCompletion("arena") { c -> arenas.keys.filter { it.startsWith(c.input) } }
            commandContexts.registerContext(ArenaBase::class.java) { c ->
                val name = c.popFirstArg()
                arenas[name] ?: throw InvalidCommandArgument("arena $name doesnt exist")
            }

            commandConditions.addCondition("lobby") { c ->
                if (Data.lobby == null) throw ConditionFailedException("you need to set a lobby first")
            }

            // error handler
            setDefaultExceptionHandler { _, _, sender, _, t ->
                sender.getIssuer<CommandSender>().apply {
                    error("we made a fucky wucky!!! (check console for exception :3)")
                    error("error is: $t")
                }
                true
            }

            registerCommand(this@Command)
        }
    }

    @HelpCommand
    fun help(sender: CommandSender, help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("reload")
    @Description("reloads plugin (for debugging)")
    @CommandPermission(ADMIN_PERM)
    fun reload(sender: CommandSender) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman reload ${PLUGIN.name}")
        sender.info("it is done")
    }

    @Subcommand("arena create")
    @Description("creates an arena by name")
    @CommandPermission(ADMIN_PERM)
    @Conditions("lobby")
    fun createArena(sender: CommandSender, name: String) {
        if (name in arenas) throw InvalidCommandArgument("arena $name already exists")
        ArenaBase(name)

        sender.info("arena $name created")
    }

    @Subcommand("arena delete")
    @Description("removes an arena by name")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arena")
    fun deleteArena(sender: CommandSender, arena: ArenaBase) {
        arena.close()
        sender.info("arena $name deleted")
    }

    @Subcommand("arena edit")
    @Description("teleports to an arena base world to edit it")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arena")
    @Conditions("lobby")
    fun editArena(sender: Player, arena: ArenaBase) {
        sender.info("teleporting to ${arena.name} base world")
        sender.teleport(arena.world.spawnLocation)
    }

    @Subcommand("lobby set")
    @Description("sets location for lobby")
    @CommandPermission(ADMIN_PERM)
    fun setLobby(sender: Player) {
        // todo set lobby
        Data.lobby = sender.location
        sender.info("lobby set to current location")
    }

    @Subcommand("lobby")
    @Description("teleports to lobby")
    @Conditions("lobby")
    fun lobby(sender: Player) {
        sender.info("teleporting to lobby")
        sender.teleport(Data.lobby!!)
    }
}
