package ru.joutak.blockparty.players

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.arenas.Arena
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

data class PlayerData(
    val playerUuid: UUID,
    var state: PlayerState,
    var currentArena: Arena?,
    val games: MutableList<UUID> = mutableListOf<UUID>(),
    var hasWon: Boolean = false
) {
    companion object {
        val playerDatas = mutableMapOf<UUID, PlayerData>()
        val dataFolder = File(PluginManager.blockParty.dataFolder.path + "/players")

        private fun create(playerUuid: UUID) : PlayerData {
            playerDatas[playerUuid] = PlayerData(playerUuid, PlayerState.LOBBY, null)
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

        fun resetGame(playerUuid: UUID) {
            val playerData = get(playerUuid)

            playerData.state = PlayerState.LOBBY
            playerData.currentArena = null

            val player = Bukkit.getPlayer(playerData.playerUuid) ?: return
            player.health = 20.0
            player.foodLevel = 20
            player.inventory.clear()
            player.level = 0
            player.exp = 0.0f
        }

        fun contains(playerUuid: UUID): Boolean {
            return playerDatas.containsKey(playerUuid) || containsInFolder(playerUuid)
        }

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

            playerDatas[uuid] = PlayerData(
                uuid,
                PlayerState.LOBBY,
                null,
                (values["games"] as MutableList<String>).map { UUID.fromString(it) }.toMutableList(),
                values["hasWon"] as Boolean
            )
            return playerDatas[uuid]!!
        }
    }

    fun isInGame() : Boolean {
        return currentArena != null && state == PlayerState.INGAME
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
        }
    }

    fun serialize(): Map<String, Any> {
        return mapOf(
            "playerUuid" to this.playerUuid.toString(),
            "games" to this.games.map { it.toString() },
            "hasWon" to this.hasWon
        )
    }
}