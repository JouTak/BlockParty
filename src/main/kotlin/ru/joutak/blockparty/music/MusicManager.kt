package ru.joutak.blockparty.music

import org.bukkit.Bukkit
import ru.joutak.blockparty.players.PlayerData
import java.util.*

object MusicManager {
    fun playFor(playersUuids: List<UUID>) {
        for (uuid in playersUuids) {
            playFor(uuid)
        }
    }

    fun playFor(playerUuid: UUID) {
        val arena = PlayerData.get(playerUuid).currentArena ?: return
        Bukkit.getServer().dispatchCommand(
            Bukkit.getConsoleSender(),
            "playsound minecraft:music.bp record ${Bukkit.getPlayer(playerUuid)?.name ?: return} ${arena.center.x} ${arena.center.y} ${arena.center.z} 0.3"
        )
    }

    fun stopFor(playersUuids: List<UUID>) {
        for (uuid in playersUuids) {
            stopFor(uuid)
        }
    }

    fun stopFor(playerUuid: UUID) {
        Bukkit.getServer().dispatchCommand(
            Bukkit.getConsoleSender(),
            "stopsound ${Bukkit.getPlayer(playerUuid)?.name ?: return} record minecraft:music.bp"
        )
    }
}