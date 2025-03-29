package ru.joutak.blockparty.utils

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.LinearComponents
import org.bukkit.*
import org.bukkit.entity.Player
import ru.joutak.blockparty.Config
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.games.GameManager
import java.util.*

object LobbyManager {
    val world : World
    private val players = LinkedHashSet<UUID>()
    private var gameStartTask: Int? = null

    init {
        if (Bukkit.getWorld("lobby") == null) {
            world = Bukkit.getWorlds()[0]
            Bukkit.getLogger().warning("Отсутствует мир lobby! В качестве лобби используется мир ${world.name}.")
        }
        else
            world = Bukkit.getWorld("lobby")!!

        val worldManager = PluginManager.multiverseCore.mvWorldManager
        worldManager.setFirstSpawnWorld(world.name)
        val mvWorld = worldManager.getMVWorld(world)

        mvWorld.setTime("day")
        mvWorld.setEnableWeather(false)
        mvWorld.setDifficulty(Difficulty.PEACEFUL)
        mvWorld.setGameMode(GameMode.ADVENTURE)
        mvWorld.setPVPMode(false)
        mvWorld.hunger = false

        world.setGameRule(GameRule.FALL_DAMAGE, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
    }

    fun addPlayer(player: Player) {
//        Bukkit.getLogger().info("${player.name} added the player!")
        PluginManager.multiverseCore.teleportPlayer(Bukkit.getConsoleSender(), player, world.spawnLocation)
        players.add(player.uniqueId)
        check()
    }

    fun removePlayer(player: Player) {
//        Bukkit.getLogger().info("${player.name} removed the player!")
        players.remove(player.uniqueId)
        check()
    }

    fun getPlayers(): List<UUID> {
        return players.toList()
    }

    fun resetTask() {
        gameStartTask = null
    }

    fun check() {
//        Bukkit.getLogger().info("${world.playerCount} в лобби")
//        Bukkit.getLogger().info("${Config.PLAYERS_TO_START} needed")
//        Bukkit.getLogger().info("count: ${players.count()}, ready arena: ${ArenaManager.hasReadyArena()}, game task: ${gameStartTask}")
        if (players.count() >= Config.PLAYERS_TO_START && ArenaManager.hasReadyArena() && gameStartTask == null) {
//            val game = GameManager.prepareGame(players.slice(0..<Config.PLAYERS_TO_START))
            Bukkit.getServer().broadcast(LinearComponents.linear(text("Игра начнется через ${Config.TIME_TO_START_GAME_LOBBY} секунд!")))
            gameStartTask = Bukkit.getScheduler().runTaskLater(PluginManager.blockParty, Runnable {
                Bukkit.getServer().broadcast(LinearComponents.linear(text("Игра началась!")))
                GameManager.startGame()
            }, Config.TIME_TO_START_GAME_LOBBY*20L).taskId
        }
        else if (players.count() < Config.PLAYERS_TO_START && gameStartTask != null) {
            Bukkit.getServer().broadcast(LinearComponents.linear(text("Недостаточно игроков для начала игры!")))
            Bukkit.getScheduler().cancelTask(gameStartTask!!)
            gameStartTask = null
        }
    }
}