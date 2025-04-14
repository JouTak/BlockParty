package ru.joutak.blockparty

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.commands.BlockPartyCommandExecutor
import ru.joutak.blockparty.listeners.PlayerDropItemListener
import ru.joutak.blockparty.listeners.PlayerJoinListener
import ru.joutak.blockparty.listeners.PlayerMoveListener
import ru.joutak.blockparty.listeners.PlayerQuitListener
import ru.joutak.blockparty.lobby.LobbyReadyBossBar
import ru.joutak.blockparty.music.MusicManager
import ru.joutak.blockparty.players.PlayerData

class BlockPartyPlugin : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: BlockPartyPlugin

        val TITLE =
            LinearComponents.linear(
                Component.text("B", NamedTextColor.RED),
                Component.text("l", NamedTextColor.GOLD),
                Component.text("o", NamedTextColor.YELLOW),
                Component.text("c", NamedTextColor.GREEN),
                Component.text("k", NamedTextColor.AQUA),
                Component.text("P", NamedTextColor.BLUE),
                Component.text("a", NamedTextColor.DARK_PURPLE),
                Component.text("r", NamedTextColor.LIGHT_PURPLE),
                Component.text("t", NamedTextColor.WHITE),
                Component.text("y", NamedTextColor.GRAY),
            )
    }

    /**
     * Plugin startup logic
     */
    override fun onEnable() {
        instance = this

        // Load data, register commands, events, etc.
        loadData()
        registerEvents()
        registerCommands()
        LobbyReadyBossBar.removeAllBossBars()

        logger.info("Плагин ${pluginMeta.name} версии ${pluginMeta.version} включен!")
    }

    private fun loadData() {
        ArenaManager.loadArenas()
        MusicManager.loadMusic()
    }

    private fun registerEvents() {
        Bukkit.getPluginManager().registerEvents(PlayerJoinListener, instance)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, instance)
        Bukkit.getPluginManager().registerEvents(PlayerMoveListener, instance)
        Bukkit.getPluginManager().registerEvents(PlayerDropItemListener, instance)
    }

    private fun registerCommands() {
        getCommand("bp")?.setExecutor(BlockPartyCommandExecutor)
    }

    /**
     * Plugin shutdown logic
     */
    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) {
            PlayerData.get(player.uniqueId).saveData()
        }

        ArenaManager.saveArenas()
    }
}
