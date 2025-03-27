package ru.joutak.blockparty.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import ru.joutak.blockparty.arenas.ArenaManager

object BlockPartyInfoCommand : BlockPartyCommand("info", listOf<String>("name")) {
    override fun execute(sender: CommandSender, command: Command, string: String, args: Array<out String>): Boolean {
        if (args.size != this.args.size) {
            return false
        }

        try {
            val arena = ArenaManager.get(args[0])

            sender.sendMessage("Информация об арене ${arena.name}:")
            sender.sendMessage("Мир: ${arena.worldName}")
            sender.sendMessage("Координаты: (${arena.x1}, ${arena.y1}, ${arena.z1} ; ${arena.x2}, ${arena.y2}, ${arena.z2})")
            sender.sendMessage("Состояние: ${arena.getState()}")
        }
        catch (e: IllegalArgumentException) {
            sender.sendMessage("${e.message}")
        }

        return true
    }

    override fun getTabHint(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> ArenaManager.getArenas().keys.filter { it.startsWith(args[0], true) }
            else -> emptyList()
        }
    }
}