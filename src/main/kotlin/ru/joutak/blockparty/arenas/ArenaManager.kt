package ru.joutak.blockparty.arenas

import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.io.IOException

object ArenaManager {
    private val arenasFile = File(PluginManager.blockParty.dataFolder, "arenas.yml")
    private val arenas = mutableMapOf<String, Arena>()

    fun add(arena: Arena) {
        if (arenas.containsKey(arena.name)) {
            throw IllegalArgumentException("Арена с таким именем уже существует.")
        }

        arenas[arena.name] = arena
    }

    fun get(name: String): Arena? = arenas[name]

    fun contains(name: String): Boolean = arenas.containsKey(name)

    fun getArenas(): Map<String, Arena> = arenas

    fun getReadyArena(): Arena? {
        for (arena in arenas.values) {
            if (arena.getState() == ArenaState.READY) {
                return arena
            }
        }
        return null
    }

    fun remove(name: String) {
        if (!arenas.containsKey(name)) {
            throw IllegalArgumentException("Арены с таким именем не существует.")
        }

        arenas.remove(name)
    }

    fun clear() {
        arenas.clear()
    }

    fun hasReadyArena(): Boolean {
        for (arena in arenas.values) {
            if (arena.getState() == ArenaState.READY) {
                return true
            }
        }
        return false
    }

    fun loadArenas() {
        if (!arenasFile.exists()) {
            PluginManager.blockParty.saveResource("arenas.yml", true)
        }

        val arenasYaml = YamlConfiguration.loadConfiguration(arenasFile)
        val arenasList = arenasYaml.getList("arenas") as? List<Map<String, Any>> ?: return

        clear()

        for (value in arenasList) {
            try {
                add(Arena.deserialize(value))
            } catch (e: Exception) {
                PluginManager.getLogger().severe("Ошибка при загрузке зон: ${e.message}")
                break
            }
        }
    }

    fun saveArenas() {
        val arenasYaml = YamlConfiguration()

        arenasYaml.set(
            "arenas",
            arenas.values.map { value ->
                value.serialize()
            },
        )

        try {
            arenasYaml.save(arenasFile)
        } catch (e: IOException) {
            PluginManager.getLogger().severe("Ошибка при сохранении зон: ${e.message}")
        }
    }
}
