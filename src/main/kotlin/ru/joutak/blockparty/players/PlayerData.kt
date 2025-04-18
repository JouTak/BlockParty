package ru.joutak.blockparty.players

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.games.GameManager
import ru.joutak.blockparty.lobby.LobbyManager
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.UUID

data class PlayerData(
    val playerUuid: UUID,
    val games: MutableList<UUID> = mutableListOf<UUID>(),
    var hasWon: Boolean = false,
) {
    private var isReady: Boolean = false

    companion object {
        val playerDatas = mutableMapOf<UUID, PlayerData>()
        val dataFolder = File(PluginManager.getDataFolder(), "players")

        init {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs()
            }
        }

        private fun create(playerUuid: UUID): PlayerData {
            playerDatas[playerUuid] = PlayerData(playerUuid)
            return playerDatas[playerUuid]!!
        }

        fun get(playerUuid: UUID): PlayerData {
            if (playerDatas.containsKey(playerUuid)) {
                return playerDatas[playerUuid]!!
            } else if (containsInFolder(playerUuid)) {
                loadFromFile(playerUuid)
            } else {
                create(playerUuid)
            }

            return playerDatas[playerUuid]!!
        }

        fun resetPlayer(playerUuid: UUID) {
            val playerData = get(playerUuid)

            val player = Bukkit.getPlayer(playerData.playerUuid) ?: return
            player.health = 20.0
            player.foodLevel = 20
            player.inventory.clear()
            player.level = 0
            player.exp = 0.0f
        }

        fun contains(playerUuid: UUID): Boolean = playerDatas.containsKey(playerUuid) || containsInFolder(playerUuid)

        private fun containsInFolder(playerUuid: UUID): Boolean {
            val files = dataFolder.listFiles() ?: return false
            for (file in files) {
                if (file.isFile && file.name.equals("$playerUuid.yml")) {
                    return true
                }
            }
            return false
        }

        private fun loadFromFile(playerUuid: UUID) {
            val fx = File(dataFolder, "$playerUuid.yml")
            if (!fx.exists()) {
                throw FileNotFoundException()
            }

            val dataFile = YamlConfiguration.loadConfiguration(fx)

            try {
                playerDatas[playerUuid] = deserialize(dataFile.getValues(true))
            } catch (e: Exception) {
                PluginManager.getLogger().severe("Ошибка при загрузке информации о игроке: ${e.message}")
            }
        }

        fun deserialize(values: Map<String, Any>): PlayerData {
            PluginManager.getLogger().info("Десериализация информации об игроке ${values["playerUuid"]}")
            val uuid = UUID.fromString(values["playerUuid"] as String)

            playerDatas[uuid] =
                PlayerData(
                    uuid,
                    (values["games"] as MutableList<String>).map { UUID.fromString(it) }.toMutableList(),
                    values["hasWon"] as Boolean,
                )
            return playerDatas[uuid]!!
        }
    }

    fun isInGame(): Boolean = GameManager.isPlaying(playerUuid)

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

    fun saveData() {
        val file = File(dataFolder, "${this.playerUuid}.yml")
        val playerData = YamlConfiguration()

        for ((path, value) in get(this.playerUuid).serialize()) {
            playerData.set(path, value)
        }

        try {
            playerData.save(file)
        } catch (e: IOException) {
            PluginManager.getLogger().severe("Ошибка при сохранении информации о игроке: ${e.message}")
        } finally {
            playerDatas.remove(this.playerUuid)
        }
    }

    fun serialize(): Map<String, Any> =
        mapOf(
            "playerUuid" to this.playerUuid.toString(),
            "games" to this.games.map { it.toString() },
            "hasWon" to this.hasWon,
        )
}
