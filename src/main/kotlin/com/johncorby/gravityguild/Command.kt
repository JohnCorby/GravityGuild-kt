package com.johncorby.gravityguild

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.*
import com.johncorby.coreapi.*
import com.johncorby.gravityguild.arena.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

private const val PERM_ADMIN = "gravityguild.admin"
private const val PERM_DEFAULT = "gravityguild.default"

@CommandAlias("gravityguild|gg")
object Command : BaseCommand() {
    init {
        PaperCommandManager(PLUGIN).apply {
            enableUnstableAPI("help")

            // arena
            commandCompletions.registerCompletion("arenaMaps") { maps.search(it) }
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

            definePermissions(
                Permission(PERM_ADMIN, "you can do everything"),
                Permission(PERM_DEFAULT, "you can do normal things", PermissionDefault.TRUE)
            )

            registerCommand(this@Command)
        }
    }

    @HelpCommand
    fun help(help: CommandHelp) = help.showHelp()


    @Subcommand("arena add")
    @Description("create an arena map by name")
    @CommandPermission(PERM_ADMIN)
    fun CommandSender.addArena(name: String) {
        commandRequire(name !in maps, "arena $name already exists")

        val world = WorldHelper.createOrLoad("$name$MAP_WORLD_SUFFIX")
        info("arena $name created")

        (this as? Player)?.let { editArena(name to world) }
    }

    @Subcommand("arena remove")
    @Description("removes an arena map by name")
    @CommandPermission(PERM_ADMIN)
    @CommandCompletion("@arenaMaps")
    fun CommandSender.removeArena(map: ArenaMap) {
        WorldHelper.delete(map.world.name)
        info("arena ${map.name} deleted")
    }

    @Subcommand("arena edit")
    @Description("teleport to an arena map to edit it")
    @CommandPermission(PERM_ADMIN)
    @CommandCompletion("@arenaMaps")
    @Conditions("lobby")
    fun Player.editArena(map: ArenaMap) {
        // todo somehow make sure there is an inventory manager
        info("teleporting to ${map.name} map world")
        teleport(map.world.spawnLocation, COMMAND)
    }

    @Subcommand("arena list")
    @Description("list arena maps and games")
    @CommandPermission(PERM_ADMIN)
    fun CommandSender.listArena() {
        info("arenas: " + maps.keys.joinToString { name ->
            name + games.filter { it.name == name }.map { it.id }.ifEmpty { "" }
        })
    }

    @Subcommand("arena join")
    @Description("join a game")
    @Conditions("lobby")
    fun Player.joinArena() = joinArena(null)

    @Subcommand("arena joins")
    @Description("join a specific game")
    @CommandPermission(PERM_ADMIN)
    @CommandCompletion("@arenaMaps")
    @Conditions("lobby")
    fun Player.joinArena(name: String?) {
        commandRequire(!inGame, "you are already in a game")
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
                info("joining game")
                teleport(world.spawnLocation, COMMAND)
            }
    }

    @Subcommand("arena leave")
    @Description("leave a game if youre in one")
    @Conditions("lobby")
    fun Player.leaveArena() {
        commandRequire(!inGame, "you are not in a game")

        info("leaving game")
        lobby(this)
    }


    @Subcommand("lobby set")
    @Description("set location for lobby")
    @CommandPermission(PERM_ADMIN)
    fun Player.setLobby() {
        Data.lobby = location
        info("lobby set to current position/direction")
    }

    @Subcommand("lobby")
    @Description("teleport to lobby")
    @Conditions("lobby")
    fun lobby(sender: Player) {
        sender.info("teleporting to lobby")
        sender.teleport(Data.lobby, COMMAND)
    }
}
