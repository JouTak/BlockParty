package ru.joutak.blockparty.games

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

    fun remove(gameUuid: UUID) {
        games.remove(gameUuid)
    }
}
