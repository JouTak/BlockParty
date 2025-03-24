package ru.joutak.blockparty

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import ru.joutak.blockparty.arenas.Arena
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.commands.BlockPartyCommandExecutor
import java.io.File
import java.io.IOException

class BlockPartyPlugin : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: BlockPartyPlugin
    }

    private var customConfig = YamlConfiguration()
    private var arenasFile = YamlConfiguration()

    private fun loadConfig() {
        val fx = File(dataFolder, "config.yml")
        if (!fx.exists()) {
            saveResource("config.yml", true)
        }
    }

    private fun loadArenas() {
        val fx = File(dataFolder, "arenas.yml")
        if (!fx.exists()) return

        arenasFile = YamlConfiguration.loadConfiguration(fx)
        val arenasList = arenasFile.getList("arenas") as? List<Map<String, Any>> ?: return

        ArenaManager.clear()

        for (value in arenasList) {
            try {
                ArenaManager.add(Arena.deserialize(value))
            } catch (e: Exception) {
                logger.severe("Ошибка при загрузке зон: ${e.message}")
                break
            }
        }
    }

    private fun saveArenas() {
        val fx = File(dataFolder, "arenas.yml")
        if (!fx.exists()) {
            saveResource("arenas.yml", true)
        }

        arenasFile.set("arenas", ArenaManager.getArenas().values.map {
            value -> value.serialize()
        })

        try {
            arenasFile.save(fx)
        } catch (e: IOException) {
            logger.severe("Ошибка при сохранении зон: ${e.message}")
        }
    }

    override fun onEnable() {
        // Plugin startup logic
        instance = this
        loadConfig()
        loadArenas()

        // Register commands and events
        getCommand("bp")?.setExecutor(BlockPartyCommandExecutor)

        logger.info("${pluginMeta.name} plugin version ${pluginMeta.version} enabled!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        saveArenas()
    }
}
