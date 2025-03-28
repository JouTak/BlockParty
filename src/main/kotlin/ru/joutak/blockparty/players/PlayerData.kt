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
    var playerState: PlayerState,
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
//                Bukkit.getLogger().info("Я в первом условии $playerUuid")
                return playerDatas[playerUuid]!!
            } else if (containsInFolder(playerUuid)) {
//                Bukkit.getLogger().info("Я во втором условии $playerUuid")
                loadFromFile(playerUuid)
            } else {
//                Bukkit.getLogger().info("Я в третьем условии $playerUuid")
                create(playerUuid)
            }

            return playerDatas[playerUuid]!!
        }

        fun resetGame(playerUuid: UUID) {
            val playerData = get(playerUuid)

            playerData.playerState = PlayerState.LOBBY
            playerData.currentArena = null
        }

        fun contains(playerUuid: UUID): Boolean {
            return playerDatas.containsKey(playerUuid) || containsInFolder(playerUuid)
        }

        private fun containsInFolder(playerUuid: UUID): Boolean {
            val files = dataFolder.listFiles() ?: return false
            for (file in files) {
                Bukkit.getLogger().info(playerUuid.toString() + "check")
                if (file.isFile && file.name.equals(playerUuid.toString())) {
                    return true
                }
            }
            return false
        }

        private fun loadFromFile(playerUuid: UUID) {
            val fx = File(dataFolder, playerUuid.toString())
            if (!fx.exists()) {
                throw FileNotFoundException()
            }

            val dataFile = YamlConfiguration.loadConfiguration(fx)

            try {
                playerDatas[playerUuid] = deserialize(dataFile.getValues(true))
            } catch (e: Exception) {
                Bukkit.getLogger().severe("Ошибка при загрузке информации о игроке: ${e.message}")
            }
        }

        fun deserialize(values: Map<String, Any>): PlayerData {
            Bukkit.getLogger().info("Deserializing player data of ${values["playerUuid"]}")
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
        return currentArena != null && playerState != PlayerState.LOBBY
    }

    fun saveData() {
        val file = File(dataFolder, this.playerUuid.toString())
        val playerData = YamlConfiguration()

        for ((path, value) in get(this.playerUuid).serialize()) {
            playerData.set(path, value)
        }

        try {
            playerData.save(file)
        } catch (e: IOException) {
            Bukkit.getLogger().severe("Ошибка при сохранении информации о игроке: ${e.message}")
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