package ru.joutak.blockparty.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.joutak.blockparty.BlockPartyPlugin
import ru.joutak.blockparty.lobby.LobbyManager
import ru.joutak.blockparty.lobby.LobbyReadyBossBar
import ru.joutak.blockparty.players.PlayerData

object BlockPartyReadyCommand : BlockPartyCommand("ready", emptyList()) {
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

        if (args.size != this.args.size) {
            return false
        }

        val playerData = PlayerData.get(sender.uniqueId)

        if (playerData.isInLobby()) {
            if (playerData.isReady()) {
                playerData.setReady(false)
                sender.sendMessage(
                    LinearComponents.linear(
                        Component.text("Вы "),
                        Component.text("вышли", NamedTextColor.RED),
                        Component.text(" из очереди на "),
                        BlockPartyPlugin.TITLE,
                        Component.text("!"),
                    ),
                )
            } else {
                playerData.setReady(true)
                sender.sendMessage(
                    LinearComponents.linear(
                        Component.text("Вы "),
                        Component.text("встали", NamedTextColor.GREEN),
                        Component.text(" в очередь на "),
                        BlockPartyPlugin.TITLE,
                        Component.text("!"),
                    ),
                )
            }
            LobbyManager.check()
            LobbyReadyBossBar.setFor(sender)
        } else {
            sender.sendMessage("Данную команду можно использовать только в лобби.")
        }

        return true
    }

    override fun getTabHint(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String> = emptyList()
}
