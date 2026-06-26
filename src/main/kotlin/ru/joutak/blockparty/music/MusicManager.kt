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
        if (playlist.isEmpty()) {
            PluginManager.logger.warning("[BlockParty] Список музыки пуст! Музыка не будет проигрываться.")
        } else {
            PluginManager.logger.info("[BlockParty] Загружено ${playlist.size} музыкальных треков")
        }
    }

    private fun loadPlaylist(): Set<Music>? {
        if (!musicFile.exists()) {
            PluginManager.logger.severe(
                "Отсутствует файл со списком доступной музыки (${musicFile.path}), пожалуйста, проверьте и перезагрузите плагин!",
            )
            try {
                musicFile.parentFile?.mkdirs()
                musicFile.createNewFile()
                val defaultYaml = YamlConfiguration()
                defaultYaml.set("playlist", listOf<Map<String, Any>>())
                defaultYaml.save(musicFile)
                PluginManager.logger.info("[BlockParty] Создан пустой music.yml")
            } catch (e: Exception) {
                PluginManager.logger.severe("[BlockParty] Не удалось создать music.yml: ${e.message}")
            }
            return emptySet()
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
