package ru.joutak.blockparty.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.games.GameManager
import ru.joutak.blockparty.lobby.LobbyManager

object BlockPartySpectateCommand : BlockPartyCommand("spectate", listOf("name"), "blockparty.spectator") {
    override fun execute(
        sender: CommandSender,
        command: Command,
        string: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Данную команду можно использовать только в игре.")
            return true
        }

        var startSpectating = false
        var endSpectating = false

        if (args.size == this.args.size) {
            startSpectating = true
        } else if (args.size == this.args.size - 1) {
            endSpectating = true
        }

        if (!startSpectating && !endSpectating) {
            return false
        }

        if (endSpectating) {
            GameManager.get(sender).forEach { it.removeSpectator(sender) }
            LobbyManager.teleportToLobby(sender)
            return true
        }

        // else if (startSpectating)
        val arena = ArenaManager.get(args[0])
        if (arena == null) {
            sender.sendMessage("Арены с таким именем не существует.")
            return true
        }
        val game = GameManager.get(arena)
        if (game == null) {
            sender.sendMessage("В данный момент на арене не идет игра.")
            return true
        }

        GameManager.get(sender).forEach { it.removeSpectator(sender) }
        game.addSpectator(sender)
        return true
    }

    override fun getTabHint(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String> =
        when (args.size) {
            1 -> ArenaManager.getArenas().keys.filter { it.startsWith(args[0], true) }
            else -> emptyList()
        }
}
