package ru.joutak.blockparty.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.joutak.blockparty.players.PlayerData
import ru.joutak.blockparty.players.PlayerState
import ru.joutak.blockparty.utils.LobbyManager
import ru.joutak.blockparty.utils.PluginManager

object LobbyListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.setHealth(20.0)
        player.setFoodLevel(20)

        PluginManager.multiverseCore.teleportPlayer(Bukkit.getConsoleSender(), player, LobbyManager.world.spawnLocation)
        LobbyManager.addPlayer(player)

        PlayerData(player.uniqueId, PlayerState.LOBBY, null)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        LobbyManager.removePlayer(player)
    }
}