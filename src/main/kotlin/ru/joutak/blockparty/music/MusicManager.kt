package ru.joutak.blockparty.music

import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.utils.PluginManager
import java.io.File

object MusicManager {
    private val musicFile = File(PluginManager.dataFolder, "music.yml")
    private var playlist: Set<Music> = setOf()

    fun getPlaylist(): Set<Music> = playlist

    fun load() {
        playlist = loadPlaylist() ?: setOf()
    }

    private fun loadPlaylist(): Set<Music>? {
        if (!musicFile.exists()) {
            PluginManager.logger.severe(
                "Отсутствует файл со списком доступной музыки (${musicFile.path}), пожалуйста, проверьте и перезагрузите плагин!",
            )
            return null
        }

        try {
            val musicYaml = YamlConfiguration.loadConfiguration(musicFile)
            val playlist =
                musicYaml.getList("playlist")
                    ?: throw NullPointerException("Не найден ключ playlist в файле с доступной музыкой")

            // PluginManager.logger.info(playlist.toString())
            PluginManager.logger.info("Список доступной музыки успешно загружен!")

            return playlist.toSet() as Set<Music>
        } catch (e: Exception) {
            PluginManager.logger.severe("Не удалось загрузить список доступной музыки: ${e.message}")
            return null
        }
    }
}
