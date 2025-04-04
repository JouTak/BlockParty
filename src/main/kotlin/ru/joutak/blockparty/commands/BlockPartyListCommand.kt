package ru.joutak.blockparty.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import ru.joutak.blockparty.arenas.ArenaManager

object BlockPartyListCommand : BlockPartyCommand("list", listOf<String>()) {
    override fun execute(sender: CommandSender, command: Command, string: String, args: Array<out String>): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("Недостаточно прав для использования данной команды.")
            return true
        }

        if (args.size != this.args.size) {
            return false
        }

        if (ArenaManager.getArenas().isEmpty()) {
            sender.sendMessage("Нет активных арен.")
        } else {
            sender.sendMessage("Список арен:")
            ArenaManager.getArenas().values.forEach {
                sender.sendMessage(it.name)
            }
        }

        return true
    }

    override fun getTabHint(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return emptyList()
    }
}