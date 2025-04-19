package ru.joutak.blockparty.players

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.games.SpartakiadaManager
import ru.joutak.blockparty.lobby.LobbyManager
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class PlayerData(
    val playerUuid: UUID,
    private val games: MutableList<UUID> = mutableListOf(),
    private var hasWon: Boolean = false,
) {
    private val dataFolder: File by lazy {
        val root =
            if (Config.get(ConfigKeys.SPARTAKIADA_MODE)) {
                SpartakiadaManager.spartakiadaFolder
            } else {
                PluginManager.blockParty.dataFolder
            }

        File(root, "players").apply { mkdirs() }
    }

    companion object {
        private val cache = ConcurrentHashMap<UUID, PlayerData>()

        fun get(uuid: UUID) = cache.getOrPut(uuid) { PlayerData(uuid) }

        fun reloadDatas() = cache.clear()

        fun resetPlayer(playerUuid: UUID) {
            val player = Bukkit.getPlayer(playerUuid) ?: return
            player.health = 20.0
            player.foodLevel = 20
            player.inventory.clear()
            player.level = 0
            player.exp = 0.0f
        }
    }

    private var isReady: Boolean = false
    private var file = File(dataFolder, "$playerUuid.yml")
    private val yaml: YamlConfiguration

    init {
        file.parentFile?.let { parent ->
            if (!parent.exists()) {
                parent.mkdirs()
            }
        }

        if (file.createNewFile()) {
            yaml = YamlConfiguration()
            yaml.set("nickname", Bukkit.getOfflinePlayer(playerUuid).name)
            yaml.set("playerUuid", playerUuid.toString())
            yaml.set("games", emptyList<String>())
            yaml.set("hasWon", false)
            if (Config.get(ConfigKeys.SPARTAKIADA_MODE)) {
                yaml.set("hasBalls", false)
                yaml.set("maxRounds", 0)
            }
            yaml.save(file)
        } else {
            yaml = YamlConfiguration.loadConfiguration(file)
            yaml.getStringList("games").forEach { games += UUID.fromString(it) }
            hasWon = yaml.getBoolean("hasWon")
        }
    }

    fun isInLobby(): Boolean =
        Bukkit
            .getPlayer(playerUuid)
            ?.world
            ?.name
            .equals(LobbyManager.world.name)

    fun isReady(): Boolean = isReady

    fun setReady(ready: Boolean) {
        isReady = ready
    }

    fun isWinner(): Boolean = hasWon

    fun setWin() {
        this.hasWon = true
        yaml.set("hasWon", hasWon)
        yaml.save(file)
        setBalls()
    }

    fun addGame(gameUuid: UUID) {
        games.add(gameUuid)
        yaml.set("games", games.map { it.toString() })
        yaml.save(file)
        setBalls()
    }

    fun setMaxRounds(rounds: Int) {
        if (!Config.get(ConfigKeys.SPARTAKIADA_MODE)) return

        yaml.set("maxRounds", maxOf(yaml.get("maxRounds", 0) as Int, rounds))
        yaml.save(file)
    }

    fun setBalls() {
        if (!Config.get(ConfigKeys.SPARTAKIADA_MODE)) return

        val hasBalls = hasWon || (games.size >= Config.get(ConfigKeys.SPARTAKIADA_ATTEMPTS))
        yaml.set("hasBalls", hasBalls)
        yaml.save(file)
    }

    fun getGames(): List<UUID> = games

    fun save() {
        try {
            yaml.set("nickname", Bukkit.getOfflinePlayer(playerUuid).name)
            yaml.save(file)
        } catch (e: IOException) {
            PluginManager.getLogger().severe("Ошибка при сохранении информации о игроке: ${e.message}")
        } finally {
            cache.remove(playerUuid)
        }
    }
}
