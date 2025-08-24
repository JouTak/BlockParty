package ru.joutak.blockparty.utils

import com.onarandombox.MultiverseCore.MultiverseCore
import org.bukkit.Bukkit
import ru.joutak.blockparty.BlockPartyPlugin

object PluginManager {
    val blockParty: BlockPartyPlugin = BlockPartyPlugin.instance
    val logger = blockParty.logger
    val dataFolder = blockParty.dataFolder

    val multiverseCore: MultiverseCore = Bukkit.getServer().pluginManager.getPlugin("Multiverse-Core") as MultiverseCore
}
