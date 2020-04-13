package com.johncorby.gravityguild

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import com.johncorby.gravityguild.arena.ArenaWorld
import com.johncorby.gravityguild.arena.arenaWorlds
import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.World
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
            commandContexts.registerContext(World::class.java) { c ->
                val name = c.popFirstArg()
                arenaWorlds[name] ?: throw InvalidCommandArgument("arena $name doesnt exist")
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
        server.dispatchCommand(CONSOLE, "plugman reload ${PLUGIN.name}")
        sender.info("it is done")
    }

    @Subcommand("arena create")
    @Description("creates an arena by name")
    @CommandPermission(ADMIN_PERM)
    fun createArena(sender: CommandSender, name: String) {
        if (name in arenaWorlds) throw InvalidCommandArgument("arena $name already exists")
        ArenaWorld.create(name)

        sender.info("arena $name created")
    }

    @Subcommand("arena delete")
    @Description("removes an arena by name")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arenaWorld")
    fun deleteArena(sender: CommandSender, arenaWorld: World) {
        ArenaWorld.delete(arenaWorld)

        sender.info("arena $name deleted")
    }

    @Subcommand("arena edit")
    @Description("teleports you to an arena world to edit it")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arenaWorld")
    @Conditions("lobby")
    fun editArena(sender: Player, arenaWorld: World) {
        sender.info("teleporting to ${arenaWorld.name} base world")
        sender.teleport(arenaWorld.spawnLocation)
    }

    @Subcommand("arena join")
    @Description("join a game")
    @Conditions("lobby")
    fun joinArena(sender: Player) {
        // todo
    }

    @Subcommand("lobby set")
    @Description("sets location for lobby")
    @CommandPermission(ADMIN_PERM)
    fun setLobby(sender: Player) {
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
