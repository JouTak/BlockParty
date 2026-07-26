package ru.joutak.blockparty.utils

import org.bukkit.Bukkit
import org.mvplugins.multiverse.core.MultiverseCore
import ru.joutak.blockparty.BlockPartyPlugin

object PluginManager {
    val blockParty: BlockPartyPlugin = BlockPartyPlugin.instance
    val logger = blockParty.logger
    val dataFolder = blockParty.dataFolder

    val multiverseCore: MultiverseCore = Bukkit.getServer().pluginManager.getPlugin("Multiverse-Core") as MultiverseCore
}
