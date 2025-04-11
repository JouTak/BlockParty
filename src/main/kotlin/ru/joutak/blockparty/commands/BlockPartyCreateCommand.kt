package ru.joutak.blockparty.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.joutak.blockparty.arenas.Arena
import ru.joutak.blockparty.arenas.ArenaManager

object BlockPartyCreateCommand :
    BlockPartyCommand("create", listOf<String>("name", "world", "x1", "y1", "z1", "x2", "y2", "z2")) {
    override fun execute(
        sender: CommandSender,
        command: Command,
        string: String,
        args: Array<out String>,
    ): Boolean {
        if (!sender.isOp) {
            sender.sendMessage("Недостаточно прав для использования данной команды.")
            return true
        }

        if (args.size != this.args.size) {
            return false
        }

        try {
            if (Bukkit.getWorld(args[1]) == null) {
                sender.sendMessage("Мира с таким именем не существует!")
                return true
            }

            val x1 = minOf(args[2].toDouble(), args[5].toDouble())
            val x2 = maxOf(args[2].toDouble(), args[5].toDouble())
            val y1 = minOf(args[3].toDouble(), args[6].toDouble())
            val y2 = maxOf(args[3].toDouble(), args[6].toDouble())
            val z1 = minOf(args[4].toDouble(), args[7].toDouble())
            val z2 = maxOf(args[4].toDouble(), args[7].toDouble())

            val newArena =
                Arena(
                    args[0],
                    args[1],
                    x1,
                    y1,
                    z1,
                    x2,
                    y2,
                    z2,
                )
            ArenaManager.add(newArena)
            sender.sendMessage("Добавлена арена с именем ${newArena.name}.")
        } catch (e: NumberFormatException) {
            sender.sendMessage("Координаты должны быть числами.")
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("${e.message}")
        }

        return true
    }

    override fun getTabHint(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String> {
        if (!sender.isOp) {
            return emptyList()
        }

        return when (args.size) {
            2 -> Bukkit.getWorlds().map { it.name }
            3 ->
                if (sender is Player) {
                    listOf(
                        sender.location.x
                            .toInt()
                            .toString(),
                    )
                } else {
                    emptyList()
                }

            4 ->
                if (sender is Player) {
                    listOf(
                        sender.location.y
                            .toInt()
                            .toString(),
                    )
                } else {
                    emptyList()
                }

            5 ->
                if (sender is Player) {
                    listOf(
                        sender.location.z
                            .toInt()
                            .toString(),
                    )
                } else {
                    emptyList()
                }
            else -> emptyList()
        }
    }
}
