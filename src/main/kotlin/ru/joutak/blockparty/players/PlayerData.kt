package ru.joutak.blockparty.players

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.games.GameManager
import ru.joutak.blockparty.lobby.LobbyManager
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.io.IOException
import java.util.UUID

data class PlayerData(
    val nickname: String,
    private val games: MutableList<UUID> = mutableListOf(),
    private var hasWon: Boolean = false,
) {
    private var isReady: Boolean = false
    private val file = File(dataFolder, "${this.nickname}.yml")
    private val playerData: YamlConfiguration

    companion object {
        private val playerDatas = mutableMapOf<String, PlayerData>()
        private var dataFolder = File(PluginManager.getDataFolder(), "players")

        init {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs()
            }
        }

        fun get(playerUuid: UUID): PlayerData = get(Bukkit.getOfflinePlayer(playerUuid).name!!)

        fun get(nickname: String): PlayerData {
            if (!playerDatas.containsKey(nickname)) {
                playerDatas[nickname] = PlayerData(nickname)
            }

            return playerDatas[nickname]!!
        }

        fun resetPlayer(playerUuid: UUID) {
            val player = Bukkit.getPlayer(playerUuid) ?: return
            player.health = 20.0
            player.foodLevel = 20
            player.inventory.clear()
            player.level = 0
            player.exp = 0.0f
        }

        fun reloadDatas() {
            playerDatas.clear()
            dataFolder = File(PluginManager.getDataFolder(), "players")
        }
    }

    init {
        if (file.createNewFile()) {
            playerData = YamlConfiguration()
            playerData.set("nickname", nickname)
            playerData.set("playerUuid", Bukkit.getOfflinePlayer(nickname).uniqueId.toString())
            playerData.set("games", emptyList<String>())
            playerData.set("maxRounds", 0)
            playerData.set("hasWon", false)
            playerData.set("hasBalls", false)
            playerData.save(file)
        } else {
            playerData = YamlConfiguration.loadConfiguration(file)
            for (game in playerData.get("games") as List<String>) {
                games.add(UUID.fromString(game))
            }
            hasWon = playerData.get("hasWon") as Boolean
        }
    }

    fun isInGame(): Boolean = GameManager.isPlaying(Bukkit.getOfflinePlayer(nickname).uniqueId)

    fun isInLobby(): Boolean =
        Bukkit
            .getPlayer(nickname)
            ?.world
            ?.name
            .equals(LobbyManager.world.name)

    fun isReady(): Boolean = isReady

    fun setReady(ready: Boolean) {
        isReady = ready
    }

    fun hasWon(): Boolean = hasWon

    fun hasWon(hasWon: Boolean) {
        this.hasWon = hasWon
        playerData.set("hasWon", hasWon)
        playerData.save(file)
        hasBalls()
    }

    fun addGame(gameUuid: UUID) {
        games.add(gameUuid)
        playerData.set("games", games.map { it.toString() })
        playerData.save(file)
        hasBalls()
    }

    fun setMaxRounds(rounds: Int) {
        playerData.set("maxRounds", maxOf(playerData.get("maxRounds", 0) as Int, rounds))
        playerData.save(file)
    }

    fun hasBalls(): Boolean {
        val hasBalls = hasWon || (games.size >= Config.get(ConfigKeys.SPARTAKIADA_ATTEMPTS))
        playerData.set("hasBalls", hasBalls)
        playerData.save(file)
        return hasBalls
    }

    fun getGames(): List<UUID> = games

    fun saveData() {
        try {
            playerData.save(file)
        } catch (e: IOException) {
            PluginManager.getLogger().severe("Ошибка при сохранении информации о игроке: ${e.message}")
        } finally {
            playerDatas.remove(this.nickname)
        }
    }
}
