package ru.joutak.blockparty

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.commands.BlockPartyCommandExecutor
import ru.joutak.blockparty.listeners.LobbyListener
import java.io.File
import java.io.IOException

class BlockPartyPlugin : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: BlockPartyPlugin
    }

    override fun onEnable() {
        // Plugin startup logic
        instance = this
        ArenaManager.loadArenas()

        // Register commands and events
        Bukkit.getPluginManager().registerEvents(LobbyListener, this)
        getCommand("bp")?.setExecutor(BlockPartyCommandExecutor)


        logger.info("${pluginMeta.name} plugin version ${pluginMeta.version} enabled!")
    }

    override fun onDisable() {
        // Plugin shutdown logic


        ArenaManager.saveArenas()
    }
}
