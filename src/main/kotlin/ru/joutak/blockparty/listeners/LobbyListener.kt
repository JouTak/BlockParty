package ru.joutak.blockparty.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.joutak.blockparty.games.GameManager
import ru.joutak.blockparty.games.GamePhase
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.utils.LobbyManager

object LobbyListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        PlayerData.resetGame(player.uniqueId)
        LobbyManager.addPlayer(player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val playerData = PlayerData.get(player.uniqueId)
        val lastGame = GameManager.get(playerData.games.last())

        if (lastGame != null && lastGame.getPhase() != GamePhase.FINISH) {
            lastGame.removePlayer(player.uniqueId)
            PlayerData.resetGame(player.uniqueId)
        }

        LobbyManager.removePlayer(player)
        playerData.saveData()
    }
}