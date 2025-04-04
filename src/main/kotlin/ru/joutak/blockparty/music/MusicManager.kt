package ru.joutak.blockparty.music

import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import java.util.*

object MusicManager {
    fun playFor(playersUuids: List<UUID>) {
        for (uuid in playersUuids) {
            playFor(uuid)
        }
    }

    fun playFor(playerUuid: UUID) {
        Bukkit.getPlayer(playerUuid)?.let {
            it.playSound(it.location, "minecraft:music.bp", SoundCategory.RECORDS, 0.25f, 1.0f)
        }

        // val arena = PlayerData.get(playerUuid).currentArena ?: return
        // Bukkit.getServer().dispatchCommand(
        //     Bukkit.getConsoleSender(),
        //     "playsound minecraft:music.bp record ${Bukkit.getPlayer(playerUuid)?.name ?: return} ~ ~ ~ 0.25 1 0.25"
        // )
    }

    fun stopFor(playersUuids: List<UUID>) {
        for (uuid in playersUuids) {
            stopFor(uuid)
        }
    }

    fun stopFor(playerUuid: UUID) {
        Bukkit.getPlayer(playerUuid)?.stopSound("minecraft:music.bp", SoundCategory.RECORDS)

        // Bukkit.getServer().dispatchCommand(
        //     Bukkit.getConsoleSender(),
        //     "stopsound ${Bukkit.getPlayer(playerUuid)?.name ?: return} record minecraft:music.bp"
        // )
    }
}