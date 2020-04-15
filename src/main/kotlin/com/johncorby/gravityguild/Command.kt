package com.johncorby.gravityguild

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import com.johncorby.gravityguild.arena.*
import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

private const val ADMIN_PERM = "gravityguild.admin"

@CommandAlias("gravityguild|gg")
object Command : BaseCommand() {
    init {
        PaperCommandManager(PLUGIN).apply {
            enableUnstableAPI("help")

            // arena
            commandCompletions.registerCompletion("arenaWorld") { c -> arenaWorlds.keys.filter { it.startsWith(c.input) } }

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
        server.dispatchCommand(CONSOLE, "plugman reload ${PLUGIN.name}")
        sender.info("it is done")
    }


    @Subcommand("arena create")
    @Description("creates an arena by name")
    @CommandPermission(ADMIN_PERM)
    fun createArena(sender: CommandSender, name: String) {
        if (name in arenaWorlds) throw InvalidCommandArgument("arena $name already exists")

        WorldHelper.createOrLoad("$name$BASE_WORLD_SUFFIX")
        sender.info("arena $name created")

        (sender as? Player)?.let { editArena(it, name) }
    }

    @Subcommand("arena delete")
    @Description("removes an arena by name")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arenaWorld")
    fun deleteArena(sender: CommandSender, name: String) {
        val arenaWorld = arenaWorlds[name] ?: throw InvalidCommandArgument("arena $name doesnt exist")

        WorldHelper.delete(arenaWorld.name)
        sender.info("arena $name deleted")
    }

    @Subcommand("arena edit")
    @Description("teleports you to an arena world to edit it")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arenaWorld")
    @Conditions("lobby")
    fun editArena(sender: Player, name: String) {
        // todo get rid of this and instead rely on inventory manager and world manager?????
        val arenaWorld = arenaWorlds[name] ?: throw InvalidCommandArgument("arena $name doesnt exist")

        sender.info("teleporting to $name base world")
        sender.teleport(arenaWorld.spawnLocation)
    }

    @Subcommand("arena join")
    @Description("join an arena")
    @Conditions("lobby")
    fun joinArena(sender: Player) {
        if (sender.inArena) throw InvalidCommandArgument("you are already in an arena")
        if (arenaWorlds.isEmpty()) throw InvalidCommandArgument("there are currently no arenas")

        // teleport to non full game with most players in it
        // or a new game if there is none
        sender.info("joining arena")
        arenaGames
            .filter { it.numPlayers != Options.maxPlayers }
            .shuffled()
            .maxBy { it.numPlayers }
            .run { this ?: ArenaGame() }
            .run { sender.teleport(world.spawnLocation) }
    }

    @Subcommand("arena leave")
    @Description("leave the arena you are in")
    @Conditions("lobby")
    fun leaveArena(sender: Player) {
        if (!sender.inArena) throw InvalidCommandArgument("you are not in an arena")

        sender.info("leaving arena")
        lobby(sender)
    }


    @Subcommand("lobby set")
    @Description("sets location for lobby")
    @CommandPermission(ADMIN_PERM)
    fun setLobby(sender: Player) {
        Data.lobby = sender.location
        sender.info("lobby set to current position/direction")
    }

    @Subcommand("lobby")
    @Description("teleports to lobby")
    @Conditions("lobby")
    fun lobby(sender: Player) {
        sender.info("teleporting to lobby")
        sender.teleport(Data.lobby!!)
    }
}
