package ru.joutak.blockparty.games

import org.bukkit.entity.Player
import ru.joutak.blockparty.arenas.Arena
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.config.Config
import ru.joutak.blockparty.config.ConfigKeys
import ru.joutak.blockparty.lobby.LobbyManager
import java.util.UUID
import kotlin.math.min

object GameManager {
    private val games = mutableMapOf<UUID, Game>()

    fun createNewGame(): Game {
        val arena = ArenaManager.getReadyArena()!!
        val players =
            LobbyManager
                .getReadyPlayers()
                .slice(0..<min(LobbyManager.getReadyPlayers().size, Config.get(ConfigKeys.MAX_PLAYERS_IN_GAME)))
                .toMutableList()
        val game = Game(arena, players)

        games[game.uuid] = game

        return game
    }

    fun get(gameUuid: UUID?): Game? = games[gameUuid]

    /**
     * Returns current (if there is such) game in given arena
     */
    fun getByArena(arena: Arena): Game? {
        for (game in games.values) {
            if (game.arena == arena) {
                return game
            }
        }
        return null
    }

    /**
     * Returns current (if there is such) game which given player is playing
     */
    fun getByPlayer(player: Player): Game? {
        for (game in games.values) {
            if (game.hasPlayer(player)) {
                return game
            }
        }
        return null
    }

    /**
     * Returns games (if there is such) which given spectator was/is watching
     */
    fun getBySpectator(spectator: Player): Iterable<Game> {
        val result = mutableListOf<Game>()
        for (game in games.values) {
            if (game.hasSpectator(spectator)) {
                result.add(game)
            }
        }
        return result
    }

    fun isPlaying(playerUuid: UUID): Boolean {
        for (game in games.values) {
            if (game.hasPlayer(playerUuid)) {
                return true
            }
        }
        return false
    }

    fun remove(gameUuid: UUID) {
        games.remove(gameUuid)
    }
}
