package ru.joutak.blockparty.players

import ru.joutak.blockparty.arenas.Arena
import java.util.*

data class PlayerData(
    val playerUuid: UUID,
    var playerState: PlayerState,
    var currentArena: Arena?,
) {
    val games = mutableListOf<UUID>()
    var hasWon = false

    companion object {
        val players = mutableMapOf<UUID, PlayerData>()

        fun getPlayerData(playerUuid: UUID): PlayerData {
            return players[playerUuid]!!
        }
    }

    init {
        players[playerUuid] = this
    }

    fun isInGame() : Boolean {
        return currentArena != null && playerState != PlayerState.LOBBY
    }
}