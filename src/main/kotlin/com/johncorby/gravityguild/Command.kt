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
            commandCompletions.registerCompletion("arenaWorld") { c -> arenaMaps.keys.filter { it.startsWith(c.input) } }

            commandConditions.addCondition("lobby") { c ->
                Data.lobby ?: throw ConditionFailedException("you need to set a lobby first")
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


    @Subcommand("arena add")
    @Description("create an arena map by name")
    @CommandPermission(ADMIN_PERM)
    fun addArena(sender: CommandSender, name: String) {
        if (name in arenaMaps) throw InvalidCommandArgument("arena $name already exists")

        WorldHelper.createOrLoad("$name$MAP_WORLD_SUFFIX")
        sender.info("arena $name created")

        (sender as? Player)?.let { editArena(it, name) }
    }

    @Subcommand("arena remove")
    @Description("removes an arena map by name")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arenaWorld")
    fun removeArena(sender: CommandSender, name: String) {
        val arenaWorld = arenaMaps[name] ?: throw InvalidCommandArgument("arena $name doesnt exist")

        WorldHelper.delete(arenaWorld.name)
        sender.info("arena $name deleted")
    }

    @Subcommand("arena edit")
    @Description("teleport to an arena map to edit it")
    @CommandPermission(ADMIN_PERM)
    @CommandCompletion("@arenaWorld")
    @Conditions("lobby")
    fun editArena(sender: Player, name: String) {
        // fixme somehow make sure there is an inventory manager
        val arenaWorld = arenaMaps[name] ?: throw InvalidCommandArgument("arena $name doesnt exist")

        sender.info("teleporting to $name map world")
        sender.teleport(arenaWorld.spawnLocation)
    }

    @Subcommand("arena list")
    @Description("list arena maps and games")
    @CommandPermission(ADMIN_PERM)
    fun listArena(sender: CommandSender) {
        sender.info("arenas: " + arenaMaps.keys.joinToString { name ->
            name + arenaGames.filter { it.name == name }.map { it.id }.ifEmpty { "" }
        })
    }

    @Subcommand("arena join")
    @Description("join a game")
    @Conditions("lobby")
    fun joinArena(sender: Player) = joinArena(sender, null, null)

    @Subcommand("arena join")
    @Description("join a specific game")
    @CommandPermission(ADMIN_PERM)
    @Conditions("lobby")
    @CommandCompletion("@arenaWorld") // todo id tab completion
    fun joinArena(sender: Player, @Optional name: String?, @Optional id: Int?) {
        if (sender.inGame) throw InvalidCommandArgument("you are already in a game")
        if (arenaMaps.isEmpty()) throw InvalidCommandArgument("there are currently no maps")

        val game = getGame(name, id)
        sender.info("joining game")
        sender.teleport(game.world.spawnLocation)
    }

    @Subcommand("arena leave")
    @Description("leave a game if youre in one")
    @Conditions("lobby")
    fun leaveArena(sender: Player) {
        if (!sender.inGame) throw InvalidCommandArgument("you are not in a game")

        sender.info("leaving game")
        lobby(sender)
    }


    @Subcommand("lobby set")
    @Description("set location for lobby")
    @CommandPermission(ADMIN_PERM)
    fun setLobby(sender: Player) {
        Data.lobby = sender.location
        sender.info("lobby set to current position/direction")
    }

    @Subcommand("lobby")
    @Description("teleport to lobby")
    @Conditions("lobby")
    fun lobby(sender: Player) {
        sender.info("teleporting to lobby")
        sender.teleport(Data.lobby!!)
    }
}
