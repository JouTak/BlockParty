package ru.joutak.blockparty.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import ru.joutak.blockparty.games.GameManager
import ru.joutak.blockparty.games.GamePhase
import ru.joutak.blockparty.lobby.LobbyManager
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.utils.PluginManager

object PlayerQuitListener : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val playerData = PlayerData.get(player.uniqueId)
        val lastGame = GameManager.getByPlayer(player)

        Bukkit.getScheduler().runTaskLater(
            PluginManager.blockParty,
            Runnable {
                if (lastGame != null && lastGame.getPhase() != GamePhase.FINISH) {
                    PlayerData.resetPlayer(player.uniqueId)
                    lastGame.checkPlayers()
                }
                LobbyManager.removeFromReadyPlayers(player)
                playerData.saveData()
                LobbyManager.check()
            },
            5L,
        )
    }
}
