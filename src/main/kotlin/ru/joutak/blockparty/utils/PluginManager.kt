package ru.joutak.blockparty.utils

import com.onarandombox.MultiverseCore.MultiverseCore
import org.bukkit.Bukkit
import ru.joutak.blockparty.BlockPartyPlugin
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.games.SpartakiadaManager
import java.io.File
import java.util.logging.Logger

object PluginManager {
    val blockParty: BlockPartyPlugin = BlockPartyPlugin.instance
    val multiverseCore: MultiverseCore = Bukkit.getServer().pluginManager.getPlugin("Multiverse-Core") as MultiverseCore

    fun getLogger(): Logger = blockParty.logger

    fun getDataFolder(): File {
        if (Config.get(ConfigKeys.SPARTAKIADA_MODE)) {
            return SpartakiadaManager.spartakiadaFolder
        }

        return blockParty.dataFolder
    }
}
