package ru.joutak.blockparty.lobby

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.LinearComponents
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.entity.Player
import ru.joutak.blockparty.BlockPartyPlugin
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.games.GameManager
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.players.PlayerState
import ru.joutak.blockparty.utils.PluginManager
import java.util.UUID
import kotlin.math.min

object LobbyManager {
    val world: World
    private val readyPlayers = LinkedHashSet<UUID>()
    private var gameStartTask: Int? = null
    private var timeLeft: Int =
        Config.get(
            ConfigKeys.TIME_TO_START_GAME_LOBBY,
        )

    init {
        if (Bukkit.getWorld(Config.get(ConfigKeys.LOBBY_WORLD_NAME)) == null) {
            world = Bukkit.getWorlds()[0]
            PluginManager.getLogger().warning(
                "Отсутствует мир ${Config.get(ConfigKeys.LOBBY_WORLD_NAME)}! В качестве лобби используется мир ${world.name}.",
            )
        } else {
            world = Bukkit.getWorld(Config.get(ConfigKeys.LOBBY_WORLD_NAME))!!
        }

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

    fun teleportToLobby(player: Player) {
        PluginManager.multiverseCore.teleportPlayer(
            Bukkit.getConsoleSender(),
            player,
            PluginManager.multiverseCore.mvWorldManager
                .getMVWorld(world)
                .spawnLocation,
        )
        LobbyReadyBossBar.setFor(player)
        Audience.audience(player).sendMessage(
            LinearComponents.linear(
                Component.text("Для игры в "),
                BlockPartyPlugin.TITLE,
                Component.text(" введите команду "),
                Component.text("/bp ready", NamedTextColor.RED, TextDecoration.BOLD),
            ),
        )
    }

    fun removeFromReadyPlayers(player: Player) {
        readyPlayers.remove(player.uniqueId)
    }

    fun getPlayers(): List<Player> = world.players

    fun getReadyPlayers(): List<UUID> = readyPlayers.toList()

    fun getReadyPlayersAudience(): Audience =
        Audience.audience(
            readyPlayers
                .mapNotNull { Bukkit.getPlayer(it) }
                .slice(0..<min(readyPlayers.size, Config.get(ConfigKeys.MAX_PLAYERS_IN_GAME))),
        )

    fun check() {
        for (player in world.players) {
            if (PlayerData.get(player.uniqueId).state == PlayerState.READY) {
                readyPlayers.add(player.uniqueId)
            } else {
                readyPlayers.remove(player.uniqueId)
            }
        }

        if (readyPlayers.count() >= Config.get(ConfigKeys.PLAYERS_TO_START) && gameStartTask == null) {
            if (ArenaManager.hasReadyArena()) {
                startLobbyCountdown()
            } else {
                getReadyPlayersAudience().sendMessage(
                    LinearComponents.linear(
                        Component.text("Отсутствует свободная арена, пожалуйста, подождите..."),
                    ),
                )
            }

            return
        }

        if (readyPlayers.count() < Config.get(ConfigKeys.PLAYERS_TO_START)) {
            if (gameStartTask != null) {
                getReadyPlayersAudience().sendMessage(
                    LinearComponents.linear(
                        Component.text("Недостаточно игроков для начала игры!"),
                    ),
                )
                resetTask()
            }

            Audience.audience(world.players).sendMessage(
                LinearComponents.linear(
                    Component.text("Ожидание "),
                    Component.text(
                        "${Config.get(ConfigKeys.PLAYERS_TO_START) - readyPlayers.count()}",
                        NamedTextColor.GOLD,
                    ),
                    Component.text(" игроков для начала игры."),
                ),
            )
        }
    }

    private fun startLobbyCountdown() {
        timeLeft = Config.get(ConfigKeys.TIME_TO_START_GAME_LOBBY)

        gameStartTask =
            Bukkit
                .getScheduler()
                .runTaskTimer(
                    PluginManager.blockParty,
                    Runnable {
                        if (timeLeft > 0) {
                            getReadyPlayersAudience().sendMessage(
                                LinearComponents.linear(
                                    Component.text("Ваша игра начнется через "),
                                    Component.text("$timeLeft", NamedTextColor.RED),
                                    Component.text(" секунд!"),
                                ),
                            )
                            timeLeft--
                        } else {
                            GameManager.createNewGame().start()
                            resetTask()
                        }
                    },
                    0L,
                    20L,
                ).taskId
    }

    fun resetTask() {
        if (gameStartTask != null) {
            Bukkit.getScheduler().cancelTask(gameStartTask!!)
        }

        gameStartTask = null
    }
}
