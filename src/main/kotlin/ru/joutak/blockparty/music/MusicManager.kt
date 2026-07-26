package ru.joutak.blockparty.music

import org.bukkit.Bukkit
import org.bukkit.SoundCategory
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.util.UUID

class MusicManager {
    private var isMusicPlaying = false
    private var musicName: String? = null
    private val playedMusic = mutableSetOf<String>()

    companion object {
        private val musicFile = File(PluginManager.dataFolder, "music.yml")
        private val music = mutableListOf<String>()

        fun loadMusic() {
            music.clear()
            // Нельзя обращаться к playedMusic здесь, это поле экземпляра

            if (!musicFile.exists()) {
                PluginManager.logger.warning("Файл music.yml не найден! Создаю новый...")
                createDefaultMusicFile()
                return
            }

            try {
                val musicYaml = YamlConfiguration.loadConfiguration(musicFile)
                val musicList = musicYaml.getList("music")

                if (musicList.isNullOrEmpty()) {
                    PluginManager.logger.warning("Список музыки пуст!")
                    return
                }

                for (item in musicList) {
                    when (item) {
                        is String -> {
                            music.add(item)
                        }
                        is Map<*, *> -> {
                            val key = (item as Map<String, Any>)["key"] as? String
                            if (key != null) {
                                music.add(key)
                            }
                        }
                        else -> {
                            PluginManager.logger.warning("Неизвестный формат в music.yml: $item")
                        }
                    }
                }

                if (music.isEmpty()) {
                    PluginManager.logger.warning("Не удалось загрузить ни одного трека!")
                } else {
                    PluginManager.logger.info("Загружено ${music.size} музыкальных треков")
                }
            } catch (e: Exception) {
                PluginManager.logger.severe("Ошибка при загрузке музыки: ${e.message}")
                e.printStackTrace()
                createDefaultMusicFile()
            }
        }

        private fun createDefaultMusicFile() {
            try {
                musicFile.parentFile?.mkdirs()
                musicFile.createNewFile()
                val defaultYaml = YamlConfiguration()
                defaultYaml.set("music", listOf(
                    "music.bp.allstar",
                    "music.bp.angelwithashotgun",
                    "music.bp.aroundtheworld"
                ))
                defaultYaml.save(musicFile)
                music.clear()
                music.addAll(listOf(
                    "music.bp.allstar",
                    "music.bp.angelwithashotgun",
                    "music.bp.aroundtheworld"
                ))
                PluginManager.logger.info("Создан новый music.yml с примерными треками")
            } catch (e: Exception) {
                PluginManager.logger.severe("Не удалось создать music.yml: ${e.message}")
            }
        }
    }

    fun playCurrentSong(playersUuids: Iterable<UUID>) {
        if (isMusicPlaying && musicName != null) {
            playFor(playersUuids)
        }
    }

    fun playNextSong(playersUuids: Iterable<UUID>) {
        if (isMusicPlaying) return

        val playlist = getPlaylist()
        if (playlist.isEmpty()) {
            PluginManager.logger.warning("Нет доступной музыки для воспроизведения!")
            return
        }

        // Если все треки сыграны, сбрасываем
        if (playedMusic.size == playlist.size) {
            playedMusic.clear()
            PluginManager.logger.info("Все треки сыграны, начинаем заново")
        }

        // Выбираем следующий трек
        val availableMusic = playlist.filter { it !in playedMusic }
        musicName = if (availableMusic.isNotEmpty()) {
            availableMusic.random()
        } else {
            playlist.random()
        }

        playedMusic.add(musicName!!)
        playFor(playersUuids)
        isMusicPlaying = true
        PluginManager.logger.info("Играет: $musicName")
    }

    fun stopSong(playersUuids: Iterable<UUID>) {
        if (!isMusicPlaying || musicName == null) return
        stopFor(playersUuids)
        isMusicPlaying = false
        PluginManager.logger.info("Музыка остановлена")
    }

    private fun playFor(playersUuids: Iterable<UUID>) {
        val currentMusic = musicName ?: return
        val soundName = if (currentMusic.startsWith("minecraft:")) currentMusic else "minecraft:$currentMusic"

        for (uuid in playersUuids) {
            Bukkit.getPlayer(uuid)?.let { player ->
                player.playSound(
                    player.location,
                    soundName,
                    SoundCategory.RECORDS,
                    0.25f,
                    1.0f
                )
            }
        }
    }

    fun stopFor(playersUuids: Iterable<UUID>) {
        val currentMusic = musicName ?: return
        val soundName = if (currentMusic.startsWith("minecraft:")) currentMusic else "minecraft:$currentMusic"

        for (uuid in playersUuids) {
            Bukkit.getPlayer(uuid)?.stopSound(soundName, SoundCategory.RECORDS)
        }
    }

    fun isPlaying(): Boolean = isMusicPlaying
    fun getCurrentMusic(): String? = musicName

    private fun getPlaylist(): List<String> = music
}