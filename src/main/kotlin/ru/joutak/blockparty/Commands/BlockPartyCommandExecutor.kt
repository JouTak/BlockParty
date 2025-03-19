package ru.joutak.blockparty.Commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BlockPartyCommandExecutor : CommandExecutor{
    override fun onCommand(sender: CommandSender, command: Command, string: String, args: Array<out String>?): Boolean {

        if (sender !is Player) return false
        if (!sender.isOp) return false

        when (args?.getOrNull(0)){
            "start"->{
                sender.sendMessage("haiii")
            }

            

            else -> return false
        }

        return false
    }
}
