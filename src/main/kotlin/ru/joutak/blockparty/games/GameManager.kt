package ru.joutak.blockparty.games

import ru.joutak.blockparty.Config
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.utils.LobbyManager
import java.util.*
import kotlin.math.min

object GameManager {
    private val games = mutableMapOf<UUID, Game>()

    fun createNewGame(): Game {
        val arena = ArenaManager.getReadyArena()!!
        val players = LobbyManager.getReadyPlayers()
            .slice(0..<min(LobbyManager.getReadyPlayers().size, Config.MAX_PLAYERS_IN_GAME))
            .toMutableList()
        val game = Game(arena, players)

        games[game.uuid] = game

        return game
    }

    fun get(gameUuid: UUID?): Game? {
        return games[gameUuid]
    }

    fun remove(gameUuid: UUID) {
        games.remove(gameUuid)
    }
}