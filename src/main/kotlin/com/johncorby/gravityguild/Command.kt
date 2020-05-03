package com.johncorby.gravityguild

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import com.johncorby.coreapi.*
import com.johncorby.gravityguild.arena.*
import hazae41.minecraft.kutils.bukkit.server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND
import org.bukkit.permissions.PermissionDefault

private const val PERM_DEFAULT = "gravityguild.defaults"
private const val PERM_ADMIN = "gravityguild.admin"

@CommandAlias("gravityguild|gg")
object Command : BaseCommand() {
    init {
        PaperCommandManager(PLUGIN).apply {
            enableUnstableAPI("help")

            // arena
            commandCompletions.registerCompletion("arenaMap") { maps.search(it.input) }
            commandContexts.registerContext(Pair::class.java) {
                val name: String = it.popFirstArg()
                name to (maps[name] ?: commandError("arena $name doesnt exist"))
            }

            commandConditions.addCondition("lobby") {
                Data.lobby.takeUnless { it == Data.NULL_LOCATION } ?: commandError("you need to set a lobby first")
            }

            // error handler
            setDefaultExceptionHandler { _, _, sender, _, t ->
                sender.getIssuer<CommandSender>().apply {
                    error("we made a fucky wucky!!! (check console for exception :3)")
                    error("error is: $t")
                }
                true
            }

            definePermission(PERM_DEFAULT, "you can do normal things", PermissionDefault.TRUE)
            definePermission(PERM_ADMIN, "You can do everything", PermissionDefault.OP)

            registerCommand(this@Command)
        }
    }

    @HelpCommand
    fun help(sender: CommandSender, help: CommandHelp) = help.showHelp()

    @Subcommand("reload")
    @Description("reload plugin (for debugging)")
    @CommandPermission(PERM_ADMIN)
    fun reload(sender: CommandSender) {
        server.dispatchCommand(CONSOLE, "plugman reload ${PLUGIN.name}")
        sender.info("it is done")
    }


    @Subcommand("arena add")
    @Description("create an arena map by name")
    @CommandPermission(PERM_ADMIN)
    fun addArena(sender: CommandSender, name: String) {
        commandRequire(name !in maps, "arena $name already exists")

        val world = WorldHelper.createOrLoad("$name$MAP_WORLD_SUFFIX")
        sender.info("arena $name created")

        (sender as? Player)?.let { editArena(it, name to world) }
    }

    @Subcommand("arena remove")
    @Description("removes an arena map by name")
    @CommandPermission(PERM_ADMIN)
    @CommandCompletion("@arenaMap")
    fun removeArena(sender: CommandSender, map: ArenaMap) {
        WorldHelper.delete(map.world.name)
        sender.info("arena ${map.name} deleted")
    }

    @Subcommand("arena edit")
    @Description("teleport to an arena map to edit it")
    @CommandPermission(PERM_ADMIN)
    @CommandCompletion("@arenaMap")
    @Conditions("lobby")
    fun editArena(sender: Player, map: ArenaMap) {
        // todo somehow make sure there is an inventory manager
        sender.info("teleporting to ${map.name} map world")
        sender.teleport(map.world.spawnLocation, COMMAND)
    }

    @Subcommand("arena list")
    @Description("list arena maps and games")
    @CommandPermission(PERM_ADMIN)
    fun listArena(sender: CommandSender) {
        sender.info("arenas: " + maps.keys.joinToString { name ->
            name + games.filter { it.name == name }.map { it.id }.ifEmpty { "" }
        })
    }

    @Subcommand("arena join")
    @Description("join a game")
    @Conditions("lobby")
    fun joinArena(sender: Player) = joinArena(sender, null)

    @Subcommand("arena joins")
    @Description("join a specific game")
    @CommandPermission(PERM_ADMIN)
    @CommandCompletion("@arenaMap")
    @Conditions("lobby")
    fun joinArena(sender: Player, name: String?) {
        commandRequire(!sender.inGame, "you are already in a game")
        commandRequire(maps.isNotEmpty(), "there are currently no maps")

        games
            .run {
                // filter by name if necessary
                if (name != null) filter { it.name == name }
                else this
            }.run {
                // find non-full game with the most players
                filter { it.isJoinable }
                    .shuffled()
                    .maxBy { it.numAlivePlayers }
                    ?: run {
                        // or create a new one if theyre all full
                        if (name != null) ArenaGame(name)
                        else ArenaGame()
                    }
            }.run {
                // join the game
                sender.info("joining game")
                sender.teleport(world.spawnLocation, COMMAND)
            }
    }

    @Subcommand("arena leave")
    @Description("leave a game if youre in one")
    @Conditions("lobby")
    fun leaveArena(sender: Player) {
        commandRequire(!sender.inGame, "you are not in a game")

        sender.info("leaving game")
        lobby(sender)
    }


    @Subcommand("lobby set")
    @Description("set location for lobby")
    @CommandPermission(PERM_ADMIN)
    fun setLobby(sender: Player) {
        Data.lobby = sender.location
        sender.info("lobby set to current position/direction")
    }

    @Subcommand("lobby")
    @Description("teleport to lobby")
    @Conditions("lobby")
    fun lobby(sender: Player) {
        sender.info("teleporting to lobby")
        sender.teleport(Data.lobby, COMMAND)
    }
}
