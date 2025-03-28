package ru.joutak.blockparty

import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import ru.joutak.blockparty.utils.PluginManager
import java.io.File

object Config {
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

        MAX_PLAYERS_IN_GAME = config.getInt("MAX_PLAYERS_IN_GAME", 12)
        MAX_ROUND_TIME = config.getInt("MAX_ROUND_TIME", 10)
        MIN_ROUND_TIME = config.getInt("MIN_ROUND_TIME", 2)
        NUMBER_OF_FLOORS = config.getInt("NUMBER_OF_FLOORS", 1)
        PLAYERS_TO_START = config.getInt("PLAYERS_TO_START", 10)
        PLAYERS_TO_END = config.getInt("PLAYERS_TO_END", 4)
        TIME_TO_START_GAME_LOBBY = config.getInt("TIME_TO_START_GAME_LOBBY", 30)
        TIME_BETWEEN_ROUNDS = config.getInt("TIME_BETWEEN_ROUNDS", 10)
    }

    fun configureWorld(worldName: String) {
        configureWorld(Bukkit.getWorld(worldName)!!)
    }

    fun configureWorld(world: World) {
        val worldManager = PluginManager.multiverseCore.mvWorldManager
        val mvWorld = worldManager.getMVWorld(world)
        mvWorld.setDifficulty(Difficulty.PEACEFUL)
        mvWorld.setGameMode(GameMode.ADVENTURE)
        mvWorld.setPVPMode(false)
        mvWorld.setHunger(false)
    }
}