package ru.joutak.blockparty.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object BlockPartyStartCommand : BlockPartyCommand("start", emptyList()) {
    override fun execute(sender: CommandSender, command: Command, string: String, args: Array<out String>?): Boolean {
        sender.sendMessage("haii")
        return true
    }

    override fun getTabHint(argumentNumber: Int): List<String> {
        return emptyList()
    }
}