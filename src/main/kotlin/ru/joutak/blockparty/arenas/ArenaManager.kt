package ru.joutak.blockparty.arenas

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.utils.PluginManager
import java.io.File
import java.io.IOException

object ArenaManager {
    private val arenas = mutableMapOf<String, Arena>()
    private var arenasFile = YamlConfiguration()

    fun add(arena: Arena) {
        if (arenas.containsKey(arena.name))
            throw IllegalArgumentException("Арена с таким именем уже существует.")

        arenas[arena.name] = arena
    }

    fun get(name: String): Arena {
        if (!arenas.containsKey(name))
            throw IllegalArgumentException("Арены с таким именем не существует.")

        return arenas[name]!!
    }

    fun getArenas() : Map<String, Arena> {
        return arenas
    }

    fun getReadyArena() : Arena? {
        for (arena in arenas.values) {
            if (arena.getState() == ArenaState.READY)
                return arena
        }
        return null
    }

    fun remove(name: String) {
        if (!arenas.containsKey(name))
            throw IllegalArgumentException("Арены с таким именем не существует.")

        arenas.remove(name)
    }

    fun clear() {
        arenas.clear()
    }

    fun hasReadyArena(): Boolean {
        for (arena in arenas.values) {
            if (arena.getState() == ArenaState.READY)
                return true
        }
        return false
    }

    fun loadArenas() {
        val fx = File(PluginManager.blockParty.dataFolder, "arenas.yml")
        if (!fx.exists()) {
            PluginManager.blockParty.saveResource("arenas.yml", true)
        }

        arenasFile = YamlConfiguration.loadConfiguration(fx)
        val arenasList = arenasFile.getList("arenas") as? List<Map<String, Any>> ?: return

        clear()

        for (value in arenasList) {
            try {
                add(Arena.deserialize(value))
            } catch (e: Exception) {
                Bukkit.getLogger().severe("Ошибка при загрузке зон: ${e.message}")
                break
            }
        }
    }

    fun saveArenas() {
        val fx = File(PluginManager.blockParty.dataFolder, "arenas.yml")

        arenasFile.set("arenas", arenas.values.map {
                value -> value.serialize()
        })

        try {
            arenasFile.save(fx)
        } catch (e: IOException) {
            Bukkit.getLogger().severe("Ошибка при сохранении зон: ${e.message}")
        }
    }
}