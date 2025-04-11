package ru.joutak.blockparty.music

import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import java.util.*

object MusicManager {
    fun playFor(playersUuids: Iterable<UUID>) {
        for (uuid in playersUuids) {
            playFor(uuid)
        }
    }

    fun playFor(playerUuid: UUID) {
        Bukkit.getPlayer(playerUuid)?.let {
            it.playSound(it.location, "minecraft:music.bp", SoundCategory.RECORDS, 0.25f, 1.0f)
        }
    }

    fun stopFor(playersUuids: Iterable<UUID>) {
        for (uuid in playersUuids) {
            stopFor(uuid)
        }
    }

    fun stopFor(playerUuid: UUID) {
        Bukkit.getPlayer(playerUuid)?.stopSound("minecraft:music.bp", SoundCategory.RECORDS)
    }
}
