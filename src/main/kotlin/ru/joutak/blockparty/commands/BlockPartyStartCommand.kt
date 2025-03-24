package ru.joutak.blockparty.commands

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.joutak.blockparty.utils.PluginManager
import ru.joutak.blockparty.arenas.Arena
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.arenas.ArenaState

object BlockPartyStartCommand : BlockPartyCommand("start", emptyList()) {
    override fun execute(sender: CommandSender, command: Command, string: String, args: Array<out String>): Boolean {
        if (args.size != this.args.size) {
            return false
        }

        var arena : Arena? = null
        for (a in ArenaManager.getArenas().values) {
            if (a.getState() == ArenaState.READY) {
                arena = a
                a.setState(ArenaState.INGAME)
                break
            }
        }

        if (arena == null) {
            Bukkit.getLogger().warning("Нет свободных арен для игры!")
            return true
        }

        val world = Bukkit.getWorld(arena.worldName)

        if (world == null) {
            Bukkit.getLogger().severe("Ошибка при загрузке арены ${arena.name}: Мир ${arena.worldName} не существует!")
        }

        val location = Location(world, (arena.x1 + arena.x2) / 2, arena.y1 + 2, (arena.z1 + arena.z2) / 2)
        PluginManager.multiverseCore.teleportPlayer(Bukkit.getConsoleSender(), sender as Player, location)
        return true
    }

    override fun getTabHint(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return emptyList()
    }
}