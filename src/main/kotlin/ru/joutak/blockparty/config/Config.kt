package ru.joutak.blockparty.config

import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.utils.PluginManager
import java.io.File

object Config {
    private val configFile = File(PluginManager.blockParty.dataFolder, "config.yml")
    private val config: YamlConfiguration

    init {
        if (!configFile.exists()) {
            PluginManager.blockParty.saveResource("config.yml", true)
        }
        config = YamlConfiguration.loadConfiguration(configFile)
        saveDefaults()
    }

    private fun saveDefaults() {
        for (key in ConfigKeys.all) {
            if (!config.contains(key.path)) {
                config.set(key.path, key.value)
            }
        }
        config.save(configFile)
    }

    fun <T : Any> get(key: ConfigKey<T>): T {
        val value = config.get(key.path)
        return if (key.value::class.java.isInstance(value)) {
            value as T
        } else {
            key.value
        }
    }

    fun <T : Any> set(
        key: ConfigKey<T>,
        value: T,
    ) {
        config.set(key.path, value)
        config.save(configFile)
    }
}
