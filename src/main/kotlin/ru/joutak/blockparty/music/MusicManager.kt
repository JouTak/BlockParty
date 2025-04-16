package ru.joutak.blockparty.music

import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.util.UUID

class MusicManager {
    private var isMusicPlaying = false
    private var musicName = music.random()

    companion object {
        private val musicFile = File(PluginManager.blockParty.dataFolder, "music.yml")
        private val music = mutableListOf<String>()

        fun loadMusic() {
            PluginManager.blockParty.saveResource("music.yml", true)

            val musicYaml = YamlConfiguration.loadConfiguration(musicFile)
            val musicList = musicYaml.getList("music") as? List<String> ?: return

            try {
                musicList.forEach { music.add(it) }
            } catch (e: Exception) {
                PluginManager.getLogger().severe("Ошибка при загрузке списка музыки: ${e.message}")
            }
        }
    }

    fun playNextSong(playersUuids: Iterable<UUID>) {
        if (isMusicPlaying) return
        musicName = music.random()
        playFor(playersUuids)
        isMusicPlaying = true
    }

    fun stopSong(playersUuids: Iterable<UUID>) {
        if (!isMusicPlaying) return
        stopFor(playersUuids)
        isMusicPlaying = false
    }

    private fun playFor(playersUuids: Iterable<UUID>) {
        for (uuid in playersUuids) {
            Bukkit.getPlayer(uuid)?.let {
                it.playSound(it.location, "minecraft:$musicName", SoundCategory.RECORDS, 0.25f, 1.0f)
            }
        }
    }

    private fun stopFor(playersUuids: Iterable<UUID>) {
        for (uuid in playersUuids) {
            Bukkit.getPlayer(uuid)?.stopSound("minecraft:$musicName", SoundCategory.RECORDS)
        }
    }
}
