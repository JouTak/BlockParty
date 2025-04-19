package ru.joutak.blockparty.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKey
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.games.SpartakiadaManager
import ru.joutak.blockparty.players.PlayerData

object BlockPartyConfigCommand : BlockPartyCommand("config", listOf("key", "value"), "blockparty.admin") {
    override fun execute(
        sender: CommandSender,
        command: Command,
        string: String,
        args: Array<out String>,
    ): Boolean {
        var set = false
        var get = false

        if (args.size == this.args.size) {
            set = true
        } else if (args.size == this.args.size - 1) {
            get = true
        }

        if (!get && !set) {
            return false
        }

        val key = ConfigKeys.all.find { it.path.equals(args[0], ignoreCase = true) }

        if (key == null) {
            sender.sendMessage("${args[0]} не найден.")
            return true
        }

        if (get) {
            sender.sendMessage("Текущее значение ${key.path}: ${Config.get(key)}")
            return true
        }
        // else if (set)
        val value = key.parse(args[1])

        if (value == null) {
            sender.sendMessage("Не удалось преобразовать ${args[1]}.")
            return true
        }

        @Suppress("UNCHECKED_CAST")
        Config.set(key as ConfigKey<Any>, value)
        sender.sendMessage("Значение ${key.path} обновлено на $value.")

        // Костыль чтобы не делать /reload confirm ;)
        if (key.path.equals(ConfigKeys.SPARTAKIADA_MODE.path)) {
            PlayerData.reloadDatas()
            SpartakiadaManager.reload()
        }

        return true
    }

    override fun getTabHint(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): List<String> =
        when (args.size) {
            1 -> ConfigKeys.all.map { it.path }.filter { it.startsWith(args[0], true) }
            else -> emptyList()
        }
}
