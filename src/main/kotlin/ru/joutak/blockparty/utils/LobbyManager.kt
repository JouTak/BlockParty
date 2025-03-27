package ru.joutak.blockparty.utils

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.LinearComponents
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import ru.joutak.blockparty.Config
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.games.GameManager
import java.util.*

object LobbyManager {
    val world : World
    private val players = mutableListOf<UUID>()
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
        Config.configureWorld(world)
    }

    fun addPlayer(player: Player) {
//        Bukkit.getLogger().info("${player.name} added the player!")
        players.add(player.uniqueId)
        check()
    }

    fun removePlayer(player: Player) {
//        Bukkit.getLogger().info("${player.name} removed the player!")
        players.remove(player.uniqueId)
        check()
    }

    fun getPlayers(): List<UUID> {
        return players
    }

    fun check() {
//        Bukkit.getLogger().info("${world.playerCount} в лобби")
//        Bukkit.getLogger().info("${Config.PLAYERS_TO_START} needed")
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
        }
    }
}