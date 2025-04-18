package ru.joutak.blockparty.games

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.util.UUID

object SpartakiadaManager {
    val spartakiadaFolder = File(PluginManager.blockParty.dataFolder, "spartakiada")
    private val participantsFile = File(PluginManager.blockParty.dataFolder, "participants.yml")
    private val winnersFile = File(spartakiadaFolder, "winners.yml")
    private var watchThread: Thread? = null
    private val participants = mutableSetOf<String>()
    private val winners = mutableSetOf<UUID>()

    val KICK_NON_PARTICIPANT_MESSAGE =
        LinearComponents.linear(
            Component.text("☒", NamedTextColor.RED),
            Component.text(" Вы "),
            Component.text("не являетесь", NamedTextColor.RED),
            Component.text(" участником спартакиады!"),
        )

    val KICK_NO_ATTEMPTS_MESSAGE =
        LinearComponents.linear(
            Component.text("К сожалению, для вас cпартакиада "),
            Component.text("закончилась ", NamedTextColor.RED),
            Component.text("☹"),
        )

    val KICK_WINNER_MESSAGE =
        LinearComponents.linear(
            Component.text("☑", NamedTextColor.GREEN),
            Component.text(" Вы уже "),
            Component.text("прошли", NamedTextColor.GREEN),
            Component.text(" в следующий этап!"),
        )

    init {
        spartakiadaFolder.mkdirs()
        if (!participantsFile.exists()) {
            PluginManager.blockParty.saveResource("participants.yml", true)
        }
        winnersFile.createNewFile()
    }

    private fun loadParticipants() {
        val config = YamlConfiguration.loadConfiguration(participantsFile)
        participants.clear()
        config.getStringList("participants").forEach { name ->
            participants.add(name)
        }
    }

    private fun loadWinners() {
        val config = YamlConfiguration.loadConfiguration(winnersFile)
        winners.clear()
        config.getConfigurationSection("winners")?.getKeys(false)?.forEach {
            runCatching { UUID.fromString(it) }.getOrNull()?.let { winners.add(it) }
        }
    }

    fun isParticipant(player: Player): Boolean = participants.contains(player.name)

    fun hasAttempts(player: Player): Boolean = PlayerData.get(player.uniqueId).games.size < Config.get(ConfigKeys.SPARTAKIADA_ATTEMPTS)

    fun getRemainingAttempts(player: Player): Int = Config.get(ConfigKeys.SPARTAKIADA_ATTEMPTS) - PlayerData.get(player.uniqueId).games.size

    fun isWinner(player: Player): Boolean = winners.contains(player.uniqueId)

    fun markWinner(player: OfflinePlayer) {
        winners.add(player.uniqueId)
        val config = YamlConfiguration.loadConfiguration(winnersFile)
        config.set("winners.${player.uniqueId}", player.name)
        config.save(winnersFile)
    }

    fun reload() {
        loadParticipants()
        loadWinners()
        checkPlayers()
    }

    fun checkPlayers() {
        if (!Config.get(ConfigKeys.SPARTAKIADA_MODE)) return
        PluginManager.getLogger().info("Проверка текущих игроков на возможность участия в спартакиаде...")

        for (player in Bukkit.getOnlinePlayers()) {
            checkPlayer(player)
        }
    }

    fun checkPlayer(player: Player) {
        if (isWinner(player)) {
            player.kick(
                KICK_WINNER_MESSAGE,
            )
        } else if (!hasAttempts(player)) {
            player.kick(
                KICK_NO_ATTEMPTS_MESSAGE,
                PlayerKickEvent.Cause.WHITELIST,
            )
        } else if (!isParticipant(player)) {
            player.kick(
                KICK_NON_PARTICIPANT_MESSAGE,
                PlayerKickEvent.Cause.WHITELIST,
            )
        }
    }

    fun watchParticipantsChanges() {
        val participantsPath =
            PluginManager.blockParty.dataFolder
                .toPath()
                .resolve("participants.yml")
        val watchService = participantsPath.parent.fileSystem.newWatchService()

        participantsPath.parent.register(
            watchService,
            java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY,
        )

        watchThread =
            Thread {
                while (!Thread.interrupted()) {
                    val key = watchService.take()
                    for (event in key.pollEvents()) {
                        val changed = event.context() as? java.nio.file.Path ?: continue
                        if (changed.fileName.toString().equals("participants.yml", ignoreCase = true)) {
                            PluginManager
                                .getLogger()
                                .info("Обнаружено изменение participants.yml, перезагрузка списка участников...")
                            loadParticipants()
                            // PluginManager.getLogger().info(participants.joinToString("\n"))
                        }
                    }
                    key.reset()
                }
            }

        watchThread!!.isDaemon = true
        watchThread!!.start()
    }

    fun stopWatching() {
        watchThread?.interrupt()
        watchThread = null
    }
}
