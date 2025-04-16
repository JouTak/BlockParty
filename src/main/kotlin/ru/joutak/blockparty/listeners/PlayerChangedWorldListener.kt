package ru.joutak.blockparty.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import ru.joutak.blockparty.lobby.LobbyManager
import ru.joutak.blockparty.lobby.LobbyReadyBossBar

object PlayerChangedWorldListener : Listener {
    @EventHandler
    fun onPlayerChangedWorld(event: PlayerChangedWorldEvent) {
        val player = event.player

        if (event.from.name.equals(LobbyManager.world.name)) {
            LobbyReadyBossBar.removeFor(player)
        }

        if (player.location.world.name
                .equals(LobbyManager.world.name)
        ) {
            LobbyReadyBossBar.setFor(player)
        }
    }
}
