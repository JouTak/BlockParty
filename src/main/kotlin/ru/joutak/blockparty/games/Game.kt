package ru.joutak.blockparty.games

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import ru.joutak.blockparty.Config
import ru.joutak.blockparty.arenas.Arena
import ru.joutak.blockparty.arenas.ArenaState
import ru.joutak.blockparty.arenas.Floors
import ru.joutak.blockparty.utils.PluginManager
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.players.PlayerState
import ru.joutak.blockparty.utils.LobbyManager
import java.io.File
import java.io.IOException
import java.util.UUID

class Game (val arena: Arena, val players: List<UUID>) : Runnable {
    val gameUuid = UUID.randomUUID()
    val winners = mutableSetOf<UUID>()
    private var round = 1
    private var phase = GamePhase.WAIT
    private var totalTime = 0
    private var timeLeft = 0
    private var currentBlock: Material? = null
    private val gameScoreboard: GameScoreboard = GameScoreboard()
    private var gameTaskId: Int = -1


    companion object {
        val dataFolder = File(PluginManager.blockParty.dataFolder.path + "/games")

        val checkRemainingPlayers = {playerUuid : UUID -> if (Bukkit.getPlayer(playerUuid) == null) false
                                                            else Bukkit.getPlayer(playerUuid)!!.gameMode == GameMode.ADVENTURE }
    }

    fun getPhase(): GamePhase {
        return this.phase
    }

    fun start() {
        arena.reset()
        arena.setState(ArenaState.INGAME)
        phase = GamePhase.WAIT
        round = 1
        arena.setCurrentFloorId(Floors.setRandomFloorAt(arena))

        for (playerUuid in players) {
            val playerData = PlayerData.get(playerUuid)
            playerData.games.add(this.gameUuid)
            playerData.currentArena = this.arena
            playerData.state = PlayerState.INGAME

            Bukkit.getPlayer(playerUuid)?.let {
                LobbyManager.removePlayer(it)
                PluginManager.multiverseCore.teleportPlayer(Bukkit.getConsoleSender(), it, arena.center)
                gameScoreboard.setFor(it)
            }
        }

        gameTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
            PluginManager.blockParty,
            this,
            0L,
            20L
        )
    }

    override fun run() {
        gameScoreboard.update(getPlayers(checkRemainingPlayers).size, round)
        gameScoreboard.setXpBarTimer(getPlayers(checkRemainingPlayers), timeLeft, totalTime)

        when (phase) {
            GamePhase.WAIT -> startNewRound()
            GamePhase.CHOOSE_BLOCK -> chooseBlock()
            GamePhase.COUNTDOWN -> countdown()
            GamePhase.BREAK_FLOOR -> breakFloor()
            GamePhase.CHECK_PLAYERS -> checkPlayers()
            GamePhase.FINISH -> finish()
        }
    }

    private fun startNewRound() {
        Bukkit.broadcastMessage("Раунд $round начинается!")
        arena.setCurrentFloorId(Floors.setRandomFloorAt(arena))
        phase = GamePhase.CHOOSE_BLOCK
        setTime(5)
    }

    private fun chooseBlock() {
        if (timeLeft > 0) {
            timeLeft--
            return
        }

        currentBlock = Floors.getRandomBlock(arena.getCurrentFloorId())
        Bukkit.broadcastMessage("Найдите блок цвета: ${currentBlock!!.name}")

        val item = ItemStack(currentBlock!!, 1) // Создаем предмет (1 блок)

        for (player in players) {
            val inventory = Bukkit.getPlayer(player)?.inventory ?: continue
            inventory.clear()
            inventory.addItem(item) // Добавляем блок в инвентарь
        }

        phase = GamePhase.COUNTDOWN
        setTime(calculateRoundTime())
    }

    private fun countdown() {
        if (timeLeft > 0) {
            timeLeft--
            return
        }

        phase = GamePhase.BREAK_FLOOR
    }

    private fun breakFloor() {
        Floors.removeBlocksExcept(arena, currentBlock!!)

        for (player in players) {
            val inventory = Bukkit.getPlayer(player)?.inventory ?: continue
            inventory.clear()
        }

        phase = GamePhase.CHECK_PLAYERS
    }

    fun checkPlayers() {
        if (getPlayers(checkRemainingPlayers).size <= Config.PLAYERS_TO_END) {
            arena.setCurrentFloorId(Floors.setRandomFloorAt(arena))
            winners.addAll(getPlayers(checkRemainingPlayers))

            Bukkit.broadcastMessage("Игра окончена!")

            for (winner in winners) {
                PlayerData.get(winner).hasWon = true
            }

            phase = GamePhase.FINISH
            setTime(Config.TIME_BETWEEN_ROUNDS)
        } else {
            round++
            phase = GamePhase.WAIT
        }
    }

    private fun finish() {
        if (timeLeft > 0) {
            timeLeft--
            return
        }

        Bukkit.getScheduler().cancelTask(gameTaskId)
        saveGame()
        arena.reset()
        for (playerUuid in players) {
            PlayerData.resetGame(playerUuid)

            Bukkit.getPlayer(playerUuid)?.let {
                gameScoreboard.removeFor(it)
                LobbyManager.addPlayer(it)
            }
        }
    }

    private fun setTime(time: Int) {
        totalTime = time
        timeLeft = time
    }

    private fun calculateRoundTime() : Int {
        return if (Config.MAX_ROUND_TIME - round < Config.MIN_ROUND_TIME)
            Config.MIN_ROUND_TIME
        else
            Config.MAX_ROUND_TIME - round
    }

    private fun getPlayers(checker: (UUID) -> Boolean): List<UUID> {
        return players.filter { playerUuid -> checker(playerUuid) } // .also { players -> Bukkit.getLogger().info(players.toString()) }
    }

    fun saveGame() {
        val file = File(dataFolder, this.gameUuid.toString())
        val gameData = YamlConfiguration()

        for ((path, value) in this.serialize()) {
            gameData.set(path, value)
        }

        try {
            gameData.save(file)
        } catch (e: IOException) {
            Bukkit.getLogger().severe("Ошибка при сохранении информации о игроке: ${e.message}")
        }
    }

    fun serialize(): Map<String, Any> {
        return mapOf(
            "gameUuid" to this.gameUuid.toString(),
            "arena" to this.arena.name,
            "players" to this.players.map { it.toString() },
            "winners" to this.winners.map { it.toString() },
            "rounds" to this.round
        )
    }
}