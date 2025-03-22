package ru.joutak.blockparty.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

object BlockPartyCommandExecutor : CommandExecutor, TabExecutor {
    private val commands = mutableMapOf<String, BlockPartyCommand>()

    init {
        registerCommand(BlockPartyStartCommand)
    }

    private fun registerCommand(command : BlockPartyCommand) {
        commands[command.name] = command
    }

    override fun onCommand(sender: CommandSender, command: Command, string: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!sender.isOp) return false

        if (args?.getOrNull(0) in commands.keys)
            return commands[args!![0]]!!.execute(sender, command, string, args)

        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        return when (args.size) {
            1 -> commands.keys.toList()
            else -> {
                if (args[0] !in commands.keys) emptyList<String>()
                else commands[args[0]]!!.getTabHint(args.size - 1)
            }
        }
    }
}
