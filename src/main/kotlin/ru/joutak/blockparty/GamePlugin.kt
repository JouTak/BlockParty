package ru.joutak.blockparty

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import ru.joutak.blockparty.commands.BlockPartyCommandExecutor
import java.io.File

class GamePlugin : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: GamePlugin
    }

    private var customConfig = YamlConfiguration()

    private fun loadConfig() {
        val fx = File(dataFolder, "config.yml")
        if (!fx.exists()) {
            saveResource("config.yml", true)
        }
    }

    override fun onEnable() {
        // Plugin startup logic
        instance = this
        loadConfig()

        // Register commands and events
        getCommand("bp")?.setExecutor(BlockPartyCommandExecutor)

        logger.info("${pluginMeta.name} plugin version ${pluginMeta.version} enabled!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
