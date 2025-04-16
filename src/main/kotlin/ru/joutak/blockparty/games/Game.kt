package ru.joutak.blockparty.games

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.joutak.blockparty.BlockPartyPlugin
import ru.joutak.blockparty.arenas.Arena
import ru.joutak.blockparty.arenas.ArenaState
import ru.joutak.blockparty.arenas.Floors
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.lobby.LobbyManager
import ru.joutak.blockparty.music.MusicManager
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.players.PlayerState
import ru.joutak.blockparty.utils.PluginManager
import java.util.UUID

class Game(
    private val arena: Arena,
    private val players: MutableList<UUID>,
) : Runnable {
    val uuid: UUID = UUID.randomUUID()
    private val scoreboard = GameScoreboard()
    private val logger = GameLogger(this)
    private val musicManager = MusicManager()
    private val onlinePlayers = mutableSetOf<UUID>()
    private val winners = mutableSetOf<UUID>()
    private var round = 1
    private var phase = GamePhase.ROUND_START
    private var totalTime = 0
    private var timeLeft = 0
    private var currentBlock: Material? = null
    private var taskId: Int = -1

    companion object {
        val checkRemainingPlayers = { playerUuid: UUID ->
            if (Bukkit.getPlayer(playerUuid) == null) {
                false
            } else {
                PlayerData.get(playerUuid).isInGame() && Bukkit.getPlayer(playerUuid)!!.gameMode == GameMode.ADVENTURE
            }
        }
    }

    fun start() {
        arena.reset()
        arena.setState(ArenaState.INGAME)
        phase = GamePhase.ROUND_START
        round = 1
        arena.setCurrentFloorId(Floors.setRandomFloorAt(arena))

        for (playerUuid in players) {
            val playerData = PlayerData.get(playerUuid)
            playerData.games.add(this.uuid)
            playerData.currentArena = this.arena
            playerData.state = PlayerState.INGAME
            onlinePlayers.add(playerUuid)
            Bukkit.getPlayer(playerUuid)?.let {
                PluginManager.multiverseCore.teleportPlayer(Bukkit.getConsoleSender(), it, arena.center)
                LobbyManager.removeFromReadyPlayers(it)
                scoreboard.setFor(it)
            }
        }

        logger.info("Игра началась в составе из ${players.size} игроков:\n${players.joinToString("\n")}")

        taskId =
            Bukkit.getScheduler().scheduleSyncRepeatingTask(
                PluginManager.blockParty,
                this,
                0L,
                20L,
            )
    }

    override fun run() {
        scoreboard.update(getPlayers(checkRemainingPlayers).size, round)
        scoreboard.setBossBarTimer(onlinePlayers, phase, timeLeft, totalTime)

        handleMusic()
        handlePhase()
    }

    private fun handleMusic() {
        when (phase) {
            GamePhase.ROUND_START -> {
                musicManager.playNextSong(onlinePlayers)
            }

            GamePhase.BREAK_FLOOR, GamePhase.CHECK_PLAYERS, GamePhase.FINISH -> {
                musicManager.stopSong(onlinePlayers)
            }

            else -> {}
        }
    }

    private fun handlePhase() {
        when (phase) {
            GamePhase.ROUND_START -> startNewRound()
            GamePhase.CHOOSE_BLOCK -> chooseBlock()
            GamePhase.COUNTDOWN -> countdown()
            GamePhase.BREAK_FLOOR -> breakFloor()
            GamePhase.CHECK_PLAYERS -> checkPlayers()
            GamePhase.FINISH -> finish()
        }
    }

    private fun startNewRound() {
        logger.info("Раунд $round начался")

        Audience.audience(onlinePlayers.mapNotNull { Bukkit.getPlayer(it) }).showTitle(
            Title.title(
                LinearComponents.linear(
                    Component.text("Раунд $round", NamedTextColor.NAMES.values().random()),
                ),
                LinearComponents.linear(),
            ),
        )

        arena.setCurrentFloorId(Floors.setRandomFloorAt(arena))
        phase = GamePhase.CHOOSE_BLOCK

        if (round == 1) {
            setTime(Config.get(ConfigKeys.TIME_BETWEEN_ROUNDS))
        } else {
            setTime(5)
        }
    }

    private fun chooseBlock() {
        if (timeLeft > 0) {
            timeLeft--
            return
        }

        currentBlock = Floors.getRandomBlock(arena.getCurrentFloorId())

        val item = ItemStack(currentBlock!!, 1)

        for (player in onlinePlayers) {
            val inventory = Bukkit.getPlayer(player)?.inventory ?: continue
            inventory.clear()
            for (slot in 0..8) {
                inventory.setItem(slot, item)
            }
        }

        logger.info("$currentBlock выбран в раунде $round")

        phase = GamePhase.COUNTDOWN
        setTime(calculateRoundTime())
    }

    private fun countdown() {
        logger.info("$timeLeft секунд до разрушения пола")

        if (timeLeft > 0) {
            timeLeft--
            return
        }

        phase = GamePhase.BREAK_FLOOR
    }

    private fun breakFloor() {
        logger.info("Пол разрушился")
        Floors.removeBlocksExcept(arena, currentBlock!!)

        for (playerUuid in onlinePlayers) {
            val inventory = Bukkit.getPlayer(playerUuid)?.inventory ?: continue
            inventory.clear()
        }

        phase = GamePhase.CHECK_PLAYERS
    }

    fun checkPlayers() {
        for (playerUuid in onlinePlayers.filter { !PlayerData.get(it).isInGame() }) {
            logger.info("Игрок $playerUuid вышел из игры!")
            Bukkit.getPlayer(playerUuid)?.let {
                scoreboard.removeFor(it)
            }
            onlinePlayers.remove(playerUuid)
        }

        if (getPlayers(checkRemainingPlayers).size <= Config.get(ConfigKeys.PLAYERS_TO_END)) {
            arena.setCurrentFloorId(Floors.setRandomFloorAt(arena))
            winners.addAll(getPlayers(checkRemainingPlayers))

            val winnersAudience = Audience.audience(winners.mapNotNull { Bukkit.getPlayer(it) })
            winnersAudience.showTitle(
                Title.title(
                    LinearComponents.linear(
                        Component.text("Вы победили! :)", NamedTextColor.GREEN),
                    ),
                    LinearComponents.linear(),
                ),
            )

            if (winners.size > 0) {
                Audience
                    .audience(
                        Audience.audience(LobbyManager.getPlayers()),
                        Audience.audience(onlinePlayers.mapNotNull { Bukkit.getPlayer(it) }),
                    ).sendMessage(
                        LinearComponents.linear(
                            Component.text("Победителями очередной игры в "),
                            BlockPartyPlugin.TITLE,
                            Component.text(" стали:\n"),
                            Component.text(
                                winners.mapNotNull { Bukkit.getPlayer(it)?.name }.joinToString("\n"),
                                NamedTextColor.WHITE,
                                TextDecoration.BOLD,
                            ),
                        ),
                    )
            }

            logger.info("Победителями стали:\n${winners.joinToString("\n")}")
            logger.addWinners(winners)
            for (winner in winners) {
                PlayerData.get(winner).hasWon = true
            }

            phase = GamePhase.FINISH
            setTime(Config.get(ConfigKeys.TIME_BETWEEN_ROUNDS))
        } else if (phase == GamePhase.CHECK_PLAYERS) {
            round++
            phase = GamePhase.ROUND_START
        }
    }

    private fun finish() {
        if (timeLeft > 0) {
            arena.launchFireworksAtCorners()
            timeLeft--
            return
        }
        Bukkit.getScheduler().cancelTask(taskId)
        logger.saveGameResults()

        arena.reset()
        for (playerUuid in onlinePlayers) {
            PlayerData.resetGame(playerUuid)

            Bukkit.getPlayer(playerUuid)?.let {
                scoreboard.removeFor(it)
                LobbyManager.teleportToLobby(it)
            }
        }

        logger.info("Игра завершилась")

        GameManager.remove(uuid)
        logger.close()
        LobbyManager.check()
    }

    fun knockout(playerUuid: UUID) {
        logger.info("Игрок $playerUuid выбыл из игры!")

        val player = Bukkit.getPlayer(playerUuid) ?: return

        Audience.audience(player).showTitle(
            Title.title(
                LinearComponents.linear(
                    Component.text("Вы проиграли! :(", NamedTextColor.RED),
                ),
                LinearComponents.linear(),
            ),
        )

        player.gameMode = GameMode.SPECTATOR
        player.teleport(arena.center)

        if (getPhase() != GamePhase.FINISH) {
            checkPlayers()
        }
    }

    private fun setTime(time: Int) {
        totalTime = time
        timeLeft = time - 1
    }

    private fun calculateRoundTime(): Int =
        if (Config.get(ConfigKeys.MAX_ROUND_TIME) - round < Config.get(ConfigKeys.MIN_ROUND_TIME)) {
            Config.get(ConfigKeys.MIN_ROUND_TIME)
        } else {
            Config.get(ConfigKeys.MAX_ROUND_TIME) - round
        }

    private fun getPlayers(checker: (UUID) -> Boolean): List<UUID> = onlinePlayers.filter { playerUuid -> checker(playerUuid) }

    fun getPhase(): GamePhase = this.phase

    fun serialize(): Map<String, Any> =
        mapOf(
            "gameUuid" to this.uuid.toString(),
            "arena" to this.arena.name,
            "players" to this.players.map { it.toString() },
            "winners" to this.winners.map { it.toString() },
            "rounds" to this.round,
        )
}
