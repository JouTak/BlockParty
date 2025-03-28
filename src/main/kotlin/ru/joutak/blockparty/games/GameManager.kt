package ru.joutak.blockparty.games

import ru.joutak.blockparty.Config
import ru.joutak.blockparty.arenas.ArenaManager
import ru.joutak.blockparty.utils.LobbyManager
import java.util.*
import kotlin.math.min

object GameManager {
    private val games = mutableMapOf<UUID, Game>()

    fun startGame() {
        val arena = ArenaManager.getReadyArena()!!
        val players = LobbyManager.getPlayers().slice(0..<min(LobbyManager.getPlayers().size, Config.MAX_PLAYERS_IN_GAME))
        val game = Game(arena, players)

        games[game.gameUuid] = game
        LobbyManager.resetTask()

        game.start()
    }

    fun get(gameUuid: UUID): Game? {
        return games[gameUuid]
    }
}