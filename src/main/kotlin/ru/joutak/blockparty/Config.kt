package ru.joutak.blockparty

import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.utils.PluginManager
import java.io.File

object Config {
    val LOBBY_WORLD_NAME: String
    val LOG_INFO_TO_CONSOLE: Boolean
    val MAX_PLAYERS_IN_GAME: Int
    val MAX_ROUND_TIME: Int
    val MIN_ROUND_TIME: Int
    val NUMBER_OF_FLOORS: Int
    val PLAYERS_TO_START: Int
    val PLAYERS_TO_END: Int
    val TIME_TO_START_GAME_LOBBY: Int
    val TIME_BETWEEN_ROUNDS: Int

    init {
        val configFile = File(PluginManager.blockParty.dataFolder, "config.yml")
        if (!configFile.exists()) {
            PluginManager.blockParty.saveResource("config.yml", true)
        }
        val config = YamlConfiguration.loadConfiguration(configFile)

        LOBBY_WORLD_NAME = config.getString("LOBBY_WORLD_NAME", "bp_lobby")!!
        LOG_INFO_TO_CONSOLE = config.getBoolean("LOG_INFO_TO_CONSOLE")
        MAX_PLAYERS_IN_GAME = config.getInt("MAX_PLAYERS_IN_GAME")
        MAX_ROUND_TIME = config.getInt("MAX_ROUND_TIME")
        MIN_ROUND_TIME = config.getInt("MIN_ROUND_TIME")
        NUMBER_OF_FLOORS = config.getInt("NUMBER_OF_FLOORS")
        PLAYERS_TO_START = config.getInt("PLAYERS_TO_START")
        PLAYERS_TO_END = config.getInt("PLAYERS_TO_END")
        TIME_TO_START_GAME_LOBBY = config.getInt("TIME_TO_START_GAME_LOBBY")
        TIME_BETWEEN_ROUNDS = config.getInt("TIME_BETWEEN_ROUNDS")
    }
}
