package ru.joutak.blockparty.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

abstract class BlockPartyCommand(val name: String, val args: List<String>) {
    abstract fun execute(sender: CommandSender, command: Command, string: String, args: Array<out String>?): Boolean
    abstract fun getTabHint(argumentNumber: Int): List<String>
}