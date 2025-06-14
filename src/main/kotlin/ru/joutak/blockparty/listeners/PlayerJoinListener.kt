package ru.joutak.blockparty.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.joutak.blockparty.lobby.LobbyManager
import ru.joutak.blockparty.players.PlayerData

object PlayerJoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        PlayerData.resetPlayer(player.uniqueId)
        LobbyManager.teleportToLobby(player)
    }
}
